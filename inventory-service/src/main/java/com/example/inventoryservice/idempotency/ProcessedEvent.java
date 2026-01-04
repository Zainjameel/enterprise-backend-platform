package com.example.inventoryservice.idempotency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
public class ProcessedEvent {

  @Id
  private UUID eventId;

  private Instant processedAt = Instant.now();

  protected ProcessedEvent() {}

  public ProcessedEvent(UUID eventId) {
    this.eventId = eventId;
  }
}
