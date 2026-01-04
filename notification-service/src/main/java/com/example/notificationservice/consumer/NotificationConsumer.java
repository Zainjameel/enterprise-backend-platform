package com.example.notificationservice.consumer;

import com.example.notificationservice.events.OrderCreatedEvent;
import com.example.notificationservice.idempotency.ProcessedEvent;
import com.example.notificationservice.idempotency.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationConsumer {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ProcessedEventRepository repository;

  public NotificationConsumer(ProcessedEventRepository repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = "${app.kafka.topic}", groupId = "notification-group")
  @Transactional
  public void consume(String payload) throws Exception {

    OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

    if (repository.existsById(event.eventId)) {
      System.out.println("[NOTIFICATION] Duplicate event ignored: " + event.eventId);
      return;
    }

    System.out.println("[NOTIFICATION] Processing orderId=" + event.orderId +
        " customerId=" + event.customerId + " amount=" + event.amount);

    repository.save(new ProcessedEvent(event.eventId));
  }
}
