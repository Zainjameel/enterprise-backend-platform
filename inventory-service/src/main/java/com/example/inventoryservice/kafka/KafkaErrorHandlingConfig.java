package com.example.inventoryservice.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

  @Value("${app.kafka.dlqTopic}")
  private String dlqTopic;

  @Bean
  public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        template,
        (record, ex) -> new TopicPartition(dlqTopic, record.partition())
    );

    return new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 3L));
  }
}
