package com.example.paymentservice.consumer;

import com.example.paymentservice.events.OrderCreatedEvent;
import com.example.paymentservice.idempotency.ProcessedEvent;
import com.example.paymentservice.idempotency.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentConsumer {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ProcessedEventRepository processedRepo;

  public PaymentConsumer(ProcessedEventRepository processedRepo) {
    this.processedRepo = processedRepo;
  }

  @KafkaListener(topics = "${app.kafka.topic}", groupId = "payment-group")
  @Transactional
  public void onMessage(String payloadJson) throws Exception {
    OrderCreatedEvent event = objectMapper.readValue(payloadJson, OrderCreatedEvent.class);

    // Idempotency check
    if (processedRepo.existsById(event.eventId)) {
      System.out.println("[PAYMENT] Duplicate event ignored: " + event.eventId);
      return;
    }

    // Simulate payment processing
    System.out.println("[PAYMENT] Processing payment for orderId=" + event.orderId +
        " customerId=" + event.customerId + " amount=" + event.amount);

    // Mark event as processed (commit happens if no exception)
    processedRepo.save(new ProcessedEvent(event.eventId));

    // If something fails before save/commit -> retry -> DLQ
  }
}