package ar.com.smg.member_service.exception;

public class AIRecommendationException extends RuntimeException {

  public AIRecommendationException(String message) {
    super(message);
  }

  public AIRecommendationException(String message, Throwable cause) {
    super(message, cause);
  }
}
