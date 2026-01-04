package com.example.paymentservice.idempotency;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID eventId;

  @Column(nullable = false)
  private Instant processedAt = Instant.now();

  protected ProcessedEvent() {}

  public ProcessedEvent(UUID eventId) {
    this.eventId = eventId;
  }

  public UUID getEventId() { return eventId; }
}