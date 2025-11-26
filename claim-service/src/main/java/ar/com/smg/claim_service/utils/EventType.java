package ar.com.smg.claim_service.utils;


public class EventType {
  public static final String CLAIM_CREATED = "CLAIM_CREATED";
  public static final String CLAIM_REJECTED = "CLAIM_REJECTED";
  public static final String CLAIM_APPROVED = "CLAIM_APPROVED";

  private EventType() { }

  public static String getEventType(String status) {
    return switch (status) {
      case "APPROVED" -> CLAIM_APPROVED;
      case "REJECTED" -> CLAIM_REJECTED;
      default -> null;
    };
  }
}
