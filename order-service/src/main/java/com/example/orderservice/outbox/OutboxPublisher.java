package com.example.orderservice.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableScheduling
public class OutboxPublisher {

  private final OutboxEventRepository outboxRepo;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${app.kafka.topic}")
  private String topic;

  public OutboxPublisher(OutboxEventRepository outboxRepo, KafkaTemplate<String, String> kafkaTemplate) {
    this.outboxRepo = outboxRepo;
    this.kafkaTemplate = kafkaTemplate;
  }

  // publish up to 50 events every second (tune as needed)
  @Scheduled(fixedDelay = 1000)
  @Transactional
  public void publishBatch() {
    var batch = outboxRepo.findByStatusOrderByCreatedAtAsc(
        OutboxEvent.Status.NEW,
        PageRequest.of(0, 50)
    );

    for (OutboxEvent ev : batch) {
      try {
        ev.incrementAttempts();

        // Key by aggregateId keeps per-order ordering
        kafkaTemplate.send(topic, ev.getAggregateId(), ev.getPayloadJson()).get();

        ev.markPublished();
      } catch (Exception ex) {
        // Keep as NEW or mark FAILED after N attempts
        if (ev.getAttempts() >= 10) ev.markFailed();
      }
    }
  }
}