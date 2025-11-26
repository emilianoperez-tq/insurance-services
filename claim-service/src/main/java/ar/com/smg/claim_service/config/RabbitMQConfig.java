package ar.com.smg.claim_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String CLAIMS_EXCHANGE = "claims.exchange";
  public static final String CLAIMS_QUEUE = "claims.notifications.queue";
  public static final String CLAIMS_ROUTING_KEY = "claims.events";

  @Bean
  public Queue claimsQueue() {
    return new Queue(CLAIMS_QUEUE, true); // durable
  }

  @Bean
  public TopicExchange claimsExchange() {
    return new TopicExchange(CLAIMS_EXCHANGE);
  }

  @Bean
  public Binding binding(Queue claimsQueue, TopicExchange claimsExchange) {
    return BindingBuilder
            .bind(claimsQueue)
            .to(claimsExchange)
            .with(CLAIMS_ROUTING_KEY);
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // ✅ Configurar RabbitTemplate para usar JSON
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter());
    return template;
  }
}
