package ar.com.smg.claim_service.controller;

import ar.com.smg.claim_service.entity.Claim;
import ar.com.smg.claim_service.repository.ClaimRepository;
import ar.com.smg.claim_service.service.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/claims")
public class ClaimController {

  private final ClaimRepository claimRepository;
  private final CloudinaryService cloudinaryService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Claim createClaim(
          @RequestPart("claim") String claimJson,
          @RequestPart(value = "image", required = false) MultipartFile file
  ) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Claim claim = mapper.readValue(claimJson, Claim.class);

    if (file != null && !file.isEmpty()) {
      Map uploadResult = cloudinaryService.upload(file);
      String imageUrl = (String) uploadResult.get("secure_url");
      claim.setImageUrl(imageUrl); // asumimos que Claim tiene un campo imageUrl
    }

    return claimRepository.save(claim);
  }

  @GetMapping()
  public List<Claim> getClaims() {
    return claimRepository
            .findAll()
            .stream()
            .filter((clam) -> {
              return clam.getStatus().equals("PENDING");
            })
            .toList();
  }

  @GetMapping("/{id}")
  public Claim getClaim(@PathVariable Long id) {
    return claimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Claim not found"));
  }

  @PutMapping("/{id}/status")
  public Claim updateStatus(@PathVariable Long id, @RequestBody String status) {
    Claim claim = claimRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Claim not found"));
    claim.setStatus(status);
    return claimRepository.save(claim);
  }
}
