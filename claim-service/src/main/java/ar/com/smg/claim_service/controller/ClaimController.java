package ar.com.smg.claim_service.controller;

import ar.com.smg.claim_service.entity.Claim;
import ar.com.smg.claim_service.repository.ClaimRepository;
import ar.com.smg.claim_service.service.ClaimService;
import ar.com.smg.claim_service.service.CloudinaryService;
import ar.com.smg.claim_service.utils.ClaimStatus;
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

  private final ClaimService claimService;
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

    return claimService.createClaim(claim);
  }

  @GetMapping()
  public List<Claim> getClaims() {
    return claimService.getAllClaims();
  }

  @GetMapping("/{id}")
  public Claim getClaim(@PathVariable Long id) {
    return claimService.getClaimById(id);
  }

  @PutMapping(value = "/{id}/status", consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Claim updateStatus(@PathVariable Long id, @RequestBody Object request) {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> requestMap = mapper.convertValue(request, Map.class);
    String status = requestMap.get("status");

    System.out.println("Requested status update to: " + status);

    if (!status.equals(ClaimStatus.APPROVED) && !status.equals(ClaimStatus.REJECTED)) {
      throw new IllegalArgumentException("Status must be either APPROVED or REJECTED");
    }

    return claimService.updateClaimStatus(id, status);
  }
}
