# Saga Pattern Implementation with Kafka

This ecommerce project implements a **Choreography-based Saga Pattern** using Apache Kafka for distributed transaction management.

## Architecture Overview

The system uses event-driven architecture where services communicate asynchronously via Kafka topics, implementing the Saga pattern to maintain data consistency across microservices.

## Saga Flow: Order Processing

### Step 1: Order Creation
1. **Order Service** receives order request
2. Creates order with status `PENDING`
3. Publishes `OrderCreatedEvent` to `order-created` topic
4. Publishes `InventoryReservationRequestEvent` to `inventory-reservation-request` topic

### Step 2: Parallel Processing (Saga Steps)

#### A. Payment Processing
1. **Payment Service** consumes `OrderCreatedEvent`
2. Automatically initiates payment (creates payment record with status `PENDING`)
3. Processes payment (simulated - 90% success rate)
4. Publishes `PaymentCompletedEvent` to `payment-completed` topic with status:
   - `SUCCESS` → Order status updated to `PAID`
   - `FAILED` → Order status updated to `PAYMENT_FAILED`

#### B. Inventory Reservation
1. **Product Service** consumes `InventoryReservationRequestEvent`
2. Validates and reserves inventory for all products
3. Publishes `InventoryReservationResultEvent` to `inventory-reservation-result` topic with status:
   - `SUCCESS` → Order status updated to `CONFIRMED`
   - `FAILED` → Order status updated to `CANCELLED`

### Step 3: Order Status Updates
- **Order Service** consumes both `PaymentCompletedEvent` and `InventoryReservationResultEvent`
- Updates order status based on the outcomes
- Implements compensation logic (order cancellation on failures)

## Kafka Topics

| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `order-created` | Order Service | Payment Service | Triggers payment initiation |
| `payment-completed` | Payment Service | Order Service | Updates order based on payment result |
| `inventory-reservation-request` | Order Service | Product Service | Requests inventory reservation |
| `inventory-reservation-result` | Product Service | Order Service | Updates order based on inventory result |

## Saga Pattern Benefits

1. **Eventual Consistency**: Services eventually reach consistent state
2. **Decoupling**: Services don't directly call each other
3. **Resilience**: System can handle partial failures
4. **Scalability**: Asynchronous processing allows better scaling

## Running the System

### Prerequisites
1. **Kafka**: Start Kafka and Zookeeper
   ```bash
   # Start Zookeeper
   zookeeper-server-start.sh config/zookeeper.properties
   
   # Start Kafka
   kafka-server-start.sh config/server.properties
   ```

2. **Eureka Server**: Start the service discovery server
   ```bash
   cd eureka
   mvn spring-boot:run
   ```

3. **Services**: Start all microservices
   ```bash
   # Terminal 1: Order Service
   cd order-service && mvn spring-boot:run
   
   # Terminal 2: Payment Service
   cd payment-service && mvn spring-boot:run
   
   # Terminal 3: Product Service
   cd product-service && mvn spring-boot:run
   
   # Terminal 4: API Gateway
   cd api-gateway && mvn spring-boot:run
   ```

## Testing the Saga Pattern

1. **Create an Order**:
   ```bash
   POST http://localhost:8080/api/orders
   Headers: x-user-id: 1
   Body: {
     "items": [
       {"productId": 1, "quantity": 2},
       {"productId": 2, "quantity": 1}
     ]
   }
   ```

2. **Monitor Kafka Topics**:
   ```bash
   # Watch order-created topic
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order-created
   
   # Watch payment-completed topic
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment-completed
   
   # Watch inventory-reservation-result topic
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic inventory-reservation-result
   ```

3. **Check Order Status**:
   ```bash
   GET http://localhost:8080/api/orders/{orderId}
   ```

## Saga Compensation

If any step fails:
- **Payment Failed**: Order status → `PAYMENT_FAILED`
- **Inventory Reservation Failed**: Order status → `CANCELLED`
- Future enhancement: Implement compensation transactions to rollback completed steps

## Key Improvements Made

1. ✅ Replaced synchronous HTTP calls (RestTemplate/Feign) with Kafka events
2. ✅ Implemented Choreography-based Saga pattern
3. ✅ Added event-driven architecture for all order processing
4. ✅ Removed tight coupling between services
5. ✅ Added proper error handling and compensation logic

