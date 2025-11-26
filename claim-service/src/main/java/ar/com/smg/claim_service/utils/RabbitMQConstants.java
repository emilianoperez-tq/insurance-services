package ar.com.smg.claim_service.utils;

public class RabbitMQConstants {
  public static final String CLAIMS_EXCHANGE = "claims.exchange";
  public static final String CLAIMS_ROUTING_KEY = "claims.events";

  private RabbitMQConstants() { }
}
