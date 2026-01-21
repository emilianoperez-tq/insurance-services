package ar.com.smg.document_service.service.document;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentTextExtractorResolver {

  private final List<DocumentTextExtractor> extractors;

  public DocumentTextExtractorResolver(List<DocumentTextExtractor> extractors) {
    this.extractors = extractors;
  }

  public String extractText(MultipartFile file) {
    return extractors.stream()
            .filter(e -> e.supports(file.getContentType()))
            .findFirst()
            .orElseThrow(() ->
                    new IllegalArgumentException("Tipo de archivo no soportado"))
            .extract(file);
  }
}
