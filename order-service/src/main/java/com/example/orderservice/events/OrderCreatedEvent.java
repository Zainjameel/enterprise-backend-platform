package com.example.orderservice.events;

import java.util.UUID;

public class OrderCreatedEvent {
  public UUID eventId;
  public Long orderId;
  public String customerId;
  public Double amount;

  public OrderCreatedEvent() {}

  public OrderCreatedEvent(UUID eventId, Long orderId, String customerId, Double amount) {
    this.eventId = eventId;
    this.orderId = orderId;
    this.customerId = customerId;
    this.amount = amount;
  }
}