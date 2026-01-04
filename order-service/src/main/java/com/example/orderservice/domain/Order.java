package com.example.orderservice.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String customerId;

  @Column(nullable = false)
  private Double amount;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  protected Order() {}

  public Order(String customerId, Double amount) {
    this.customerId = customerId;
    this.amount = amount;
  }

  public Long getId() { return id; }
  public String getCustomerId() { return customerId; }
  public Double getAmount() { return amount; }
  public Instant getCreatedAt() { return createdAt; }
}