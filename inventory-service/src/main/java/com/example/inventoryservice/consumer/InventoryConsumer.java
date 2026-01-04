package com.example.inventoryservice.consumer;

import com.example.inventoryservice.events.OrderCreatedEvent;
import com.example.inventoryservice.idempotency.ProcessedEvent;
import com.example.inventoryservice.idempotency.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryConsumer {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ProcessedEventRepository repository;

  public InventoryConsumer(ProcessedEventRepository repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = "${app.kafka.topic}", groupId = "inventory-group")
  @Transactional
  public void consume(String payload) throws Exception {

    OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

    if (repository.existsById(event.eventId)) {
      System.out.println("[INVENTORY] Duplicate event ignored: " + event.eventId);
      return;
    }

    System.out.println("[INVENTORY] Processing orderId=" + event.orderId +
        " customerId=" + event.customerId + " amount=" + event.amount);

    repository.save(new ProcessedEvent(event.eventId));
  }
}
