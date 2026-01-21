package ar.com.smg.document_service.service.document;

import ar.com.smg.document_service.model.ClassificationResponse;
import com.google.genai.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentClassificationService {
  private final ChatClient chatClient;
  private final Client genaiClient;

  @Value("${gemini.ia.model}")
  private String modelName;

  private static final String PROMPT_TEMPLATE = """
        Clasificá el siguiente documento médico en UNA sola categoría:
        - DOCUMENTO GENERAL
        - CREDENCIAL
        - RECETA
        - ESTUDIO MEDICO
        - OTROS
        
        Tené en cuenta que el texto puede provenir de OCR y contener errores.
        
        Hay dos opciones cuando te pase el documento.
        - Te paso texto plano, porque era un PDF y lo extraje de ahí.
        - Te paso base64 porque era una imagen.
        
        Respondé SOLO con la categoría.
        
        Documento:
        %s
        """;

  public DocumentClassificationService(ChatClient chatClient, @Value("${gemini.ia.api-key}") String apiKey) {
    this.chatClient = chatClient;
    this.genaiClient = Client.builder().apiKey(apiKey).build();
  }

  public ClassificationResponse classify(String documentId, String documentText){
    String prompt = String.format(PROMPT_TEMPLATE, documentText);

    log.info("Prompt a enviar: {}", prompt);

    String response = genaiClient.models.generateContent(
            modelName,
            prompt,
            null
    ).text();

    log.info("Respuesta: {}", response);


    return new ClassificationResponse(documentId, response);
  }

}
