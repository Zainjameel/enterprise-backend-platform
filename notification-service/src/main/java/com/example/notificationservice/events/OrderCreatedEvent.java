package com.example.notificationservice.events;

import java.util.UUID;

public class OrderCreatedEvent {
  public UUID eventId;
  public Long orderId;
  public String customerId;
  public Double amount;
}
