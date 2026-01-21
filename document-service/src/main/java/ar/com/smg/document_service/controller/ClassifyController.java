package ar.com.smg.document_service.controller;

import ar.com.smg.document_service.model.ClassificationResponse;
import ar.com.smg.document_service.service.document.DocumentClassificationService;
import ar.com.smg.document_service.service.document.DocumentTextExtractorResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ia")
@Slf4j
@RequiredArgsConstructor
public class ClassifyController {
  private final DocumentTextExtractorResolver extractorResolver;
  private final DocumentClassificationService classificationService;

  @PostMapping(
          value = "/clasificar/{id}",
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<ClassificationResponse> clasificar(
          @PathVariable String id,
          @RequestPart("file") MultipartFile file
  ) {
    log.info("Solicitud de clasificación para el documento {}", id);

    String text = extractorResolver.extractText(file);

    var response = classificationService.classify(id, text);

    return ResponseEntity.ok(response);
  }
}
