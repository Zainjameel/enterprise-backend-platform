package com.example.orderservice.service;

import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import com.example.orderservice.events.OrderCreatedEvent;
import com.example.orderservice.outbox.OutboxEvent;
import com.example.orderservice.outbox.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderAppService {

  private final OrderRepository orderRepo;
  private final OutboxEventRepository outboxRepo;
  private final ObjectMapper objectMapper;

  public OrderAppService(OrderRepository orderRepo, OutboxEventRepository outboxRepo, ObjectMapper objectMapper) {
    this.orderRepo = orderRepo;
    this.outboxRepo = outboxRepo;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public Long createOrder(String customerId, Double amount) {
    // 1) Save order in DB
    Order saved = orderRepo.save(new Order(customerId, amount));

    // 2) Write Outbox event in SAME DB transaction (the key benefit)
    UUID eventId = UUID.randomUUID();
    OrderCreatedEvent dto = new OrderCreatedEvent(eventId, saved.getId(), customerId, amount);

    try {
      String payloadJson = objectMapper.writeValueAsString(dto);
      OutboxEvent outbox = new OutboxEvent(
          eventId,
          "OrderCreated",
          "Order",
          String.valueOf(saved.getId()),
          payloadJson
      );
      outboxRepo.save(outbox);
    } catch (Exception e) {
      // If JSON fails, transaction rolls back => order won't be created either
      throw new RuntimeException("Failed to serialize outbox event", e);
    }

    return saved.getId();
  }
}