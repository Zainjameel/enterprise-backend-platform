# Enterprise Backend Platform (Local Demo)

This repo contains a small "enterprise-style" Java microservices demo using:

- Spring Boot 3 (Java 17)
- Kafka (event-driven)
- PostgreSQL (JPA)
- Outbox pattern in `order-service`
- Retry + DLQ and Idempotency in consumers

## Services
- `order-service` (REST API + Postgres + Outbox Publisher -> Kafka)
- `payment-service` (Kafka consumer + idempotency + retry/DLQ)
- `inventory-service` (Kafka consumer + idempotency + retry/DLQ)
- `notification-service` (Kafka consumer + idempotency + retry/DLQ)

## Prereqs
- Java 17
- Maven
- Docker Desktop (compose v2)

## Run

1) Start Kafka + Postgres:

```bash
docker compose up -d
docker ps
```

2) Start services (each in its own terminal):

```bash
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

3) Create an order:

```bash
curl -X POST http://localhost:8081/orders -H "Content-Type: application/json" -d '{"customerId":"C123","amount":250.00}'
```

4) Open Swagger UI:
- http://localhost:8081/swagger-ui.html

## Notes
If Postgres port 5432 is already used on your machine, change docker-compose `5432:5432` to `5433:5432`,
and update each service `application.yml` datasource URL to use `localhost:5433`.
