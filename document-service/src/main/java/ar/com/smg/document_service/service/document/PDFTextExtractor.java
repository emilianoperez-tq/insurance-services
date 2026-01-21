package ar.com.smg.document_service.service.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PDFTextExtractor implements DocumentTextExtractor {

  @Override
  public boolean supports(String contentType) {
    return contentType.equalsIgnoreCase("application/pdf");
  }

  @Override
  public String extract(MultipartFile file) {
    try (PDDocument document = PDDocument.load(file.getInputStream())) {
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(document);
    } catch (IOException e) {
      throw new RuntimeException("Error leyendo PDF", e);
    }
  }
}
