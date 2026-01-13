package ar.com.smg.document_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Client s3Client;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.endpoint}")
  private String s3Endpoint;


  public String uploadDocument(MultipartFile file) throws IOException {
    try {
      String fileName = generateFileName(file.getOriginalFilename());

      log.info("Uploading file to S3: {} (bucket: {})", fileName, bucketName);

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
              .bucket(bucketName)
              .key(fileName)
              .contentType(file.getContentType())
              .build();

      s3Client.putObject(putObjectRequest,
              RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      String fileUrl = String.format("%s/%s/%s", s3Endpoint, bucketName, fileName);

      log.info("File uploaded successfully: {}", fileUrl);
      return fileUrl;

    } catch (S3Exception e) {
      log.error("Error uploading file to S3", e);
      throw new IOException("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
    }
  }

  public byte[] downloadDocument(String documentUrl) throws IOException {
    try {
      String fileName = extractFileNameFromUrl(documentUrl);

      log.info("Downloading file from S3: {}", fileName);

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
              .bucket(bucketName)
              .key(fileName)
              .build();

      byte[] data = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

      log.info("File downloaded successfully: {} bytes", data.length);
      return data;

    } catch (S3Exception e) {
      log.error("Error downloading file from S3", e);
      throw new IOException("Failed to download file from S3: " + e.awsErrorDetails().errorMessage(), e);
    }
  }

  private String generateFileName(String originalFilename) {
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    return UUID.randomUUID().toString() + extension;
  }

  private String extractFileNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }
}
