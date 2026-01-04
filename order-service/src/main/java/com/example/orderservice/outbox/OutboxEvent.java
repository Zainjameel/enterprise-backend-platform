package com.example.orderservice.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events", indexes = {
    @Index(name = "idx_outbox_status_created", columnList = "status,createdAt")
})
public class OutboxEvent {

  public enum Status { NEW, PUBLISHED, FAILED }

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false)
  private String eventType; // "OrderCreated"

  @Column(nullable = false)
  private String aggregateType; // "Order"

  @Column(nullable = false)
  private String aggregateId; // orderId

  @Lob
  @Column(nullable = false)
  private String payloadJson;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.NEW;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private int attempts = 0;

  protected OutboxEvent() {}

  public OutboxEvent(UUID id, String eventType, String aggregateType, String aggregateId, String payloadJson) {
    this.id = id;
    this.eventType = eventType;
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.payloadJson = payloadJson;
  }

  public UUID getId() { return id; }
  public String getEventType() { return eventType; }
  public String getAggregateType() { return aggregateType; }
  public String getAggregateId() { return aggregateId; }
  public String getPayloadJson() { return payloadJson; }
  public Status getStatus() { return status; }
  public Instant getCreatedAt() { return createdAt; }
  public int getAttempts() { return attempts; }

  public void markPublished() { this.status = Status.PUBLISHED; }
  public void markFailed() { this.status = Status.FAILED; }
  public void incrementAttempts() { this.attempts++; }
}
