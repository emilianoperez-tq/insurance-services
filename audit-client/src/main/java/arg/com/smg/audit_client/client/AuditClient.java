package arg.com.smg.audit_client.client;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditClient {

  private final DynamoDbClient dynamoDbClient;
  private final String tableName = "AuditLogs";

  public AuditClient(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
  }

  public PutItemResponse log(String service, String action, String userId, String payload) {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("logId", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
    item.put("timestamp", AttributeValue.builder().s(Instant.now().toString()).build());
    item.put("service", AttributeValue.builder().s(service).build());
    item.put("action", AttributeValue.builder().s(action).build());
    item.put("userId", AttributeValue.builder().s(userId).build());
    item.put("payload", AttributeValue.builder().s(payload).build());

    return dynamoDbClient.putItem(PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build());
  }
}
