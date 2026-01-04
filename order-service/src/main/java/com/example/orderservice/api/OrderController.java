package com.example.orderservice.api;

import com.example.orderservice.service.OrderAppService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderAppService appService;

  public OrderController(OrderAppService appService) {
    this.appService = appService;
  }

  public static class CreateOrderRequest {
    @NotBlank public String customerId;
    @NotNull @Positive public Double amount;
  }

  public static class CreateOrderResponse {
    public Long orderId;
    public String status;

    public CreateOrderResponse(Long orderId, String status) {
      this.orderId = orderId;
      this.status = status;
    }
  }

  @PostMapping
  public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest req) {
    Long id = appService.createOrder(req.customerId, req.amount);
    return ResponseEntity.ok(new CreateOrderResponse(id, "CREATED"));
  }
}