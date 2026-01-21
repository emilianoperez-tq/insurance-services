package ar.com.smg.document_service.service.document;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

@Service
public class ImageTextExtractor implements DocumentTextExtractor {
  public ImageTextExtractor() {
  }

  @Override
  public boolean supports(String contentType) {
    return contentType.startsWith("image/");
  }

  @Override
  public String extract(MultipartFile file) {
    try (InputStream is = file.getInputStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      BufferedImage original = ImageIO.read(is);
      if (original == null) {
        throw new IllegalArgumentException("El archivo no es una imagen válida");
      }

      BufferedImage resized = Scalr.resize(
              original,
              Scalr.Method.QUALITY,
              Scalr.Mode.AUTOMATIC,
              256,
              256
      );

      BufferedImage gray = new BufferedImage(
              resized.getWidth(),
              resized.getHeight(),
              BufferedImage.TYPE_BYTE_GRAY
      );

      Graphics2D g = gray.createGraphics();
      g.drawImage(resized, 0, 0, null);
      g.dispose();

      ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
      ImageWriteParam param = writer.getDefaultWriteParam();

      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(0.7f);

      writer.setOutput(ImageIO.createImageOutputStream(baos));
      writer.write(null, new IIOImage(gray, null, null), param);
      writer.dispose();

      return Base64.getEncoder().encodeToString(baos.toByteArray());

    } catch (Exception e) {
      throw new RuntimeException("Error procesando imagen para IA", e);
    }
  }
}
