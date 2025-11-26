package ar.com.smg.notification_service.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
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

  // ✅ Converter a JSON (igual que en Producer)
  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // ✅ Configurar el Listener para usar JSON
  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
          ConnectionFactory connectionFactory,
          SimpleRabbitListenerContainerFactoryConfigurer configurer) {

    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }
}
