# E-Commerce Microservices with Kafka and Saga Pattern

A comprehensive e-commerce microservices application demonstrating **Apache Kafka** for event-driven architecture and **Saga Pattern** for distributed transaction management.

## 🏗️ Architecture

This project consists of multiple microservices:

- **API Gateway** (Port 8080) - Single entry point for all requests
- **Eureka Server** (Port 8761) - Service discovery
- **User Service** (Port 8081) - User management and authentication
- **Product Service** (Port 8082) - Product catalog and inventory management
- **Order Service** (Port 8083) - Order processing with Saga orchestration
- **Cart Service** (Port 8084) - Shopping cart management
- **Payment Service** (Port 8085) - Payment processing

## 🎯 Key Features

### Event-Driven Architecture
- All inter-service communication via Kafka topics
- Asynchronous message processing
- Decoupled microservices

### Saga Pattern Implementation
- **Choreography-based Saga** for order processing
- Automatic compensation on failures
- Eventual consistency across services

### Order Processing Flow
1. Order created → Publishes events
2. Payment service processes payment asynchronously
3. Product service reserves inventory asynchronously
4. Order status updated based on outcomes

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Apache Kafka (see [KAFKA_SETUP.md](KAFKA_SETUP.md))
- Eureka Server running

## 🚀 Quick Start

### 1. Start Kafka
```bash
# Using Docker (recommended)
docker-compose up -d

# Or manually (see KAFKA_SETUP.md)
```

### 2. Start Eureka Server
```bash
cd eureka
mvn spring-boot:run
```

### 3. Start Services
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

### 4. Test the System
```bash
# Create an order
POST http://localhost:8080/api/orders
Headers: x-user-id: 1
Body: {
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 2, "quantity": 1}
  ]
}

# Check order status
GET http://localhost:8080/api/orders/{orderId}
```

## 📚 Documentation

- [SAGA_PATTERN.md](SAGA_PATTERN.md) - Detailed Saga pattern explanation
- [KAFKA_SETUP.md](KAFKA_SETUP.md) - Kafka installation and setup guide

## 🔄 Saga Pattern Flow

```
Order Created
    ├──→ Payment Service (processes payment)
    │       └──→ PaymentCompletedEvent
    │
    └──→ Product Service (reserves inventory)
            └──→ InventoryReservationResultEvent

Order Service (consumes both events)
    └──→ Updates order status
```

## 🛠️ Technology Stack

- **Spring Boot 3.3.4**
- **Spring Cloud 2023.0.3**
- **Apache Kafka** - Event streaming
- **Spring Kafka** - Kafka integration
- **Eureka** - Service discovery
- **H2 Database** - In-memory database (for development)
- **PostgreSQL** - Production database support
- **Maven** - Build tool

## 📝 Key Improvements Made

✅ Replaced synchronous HTTP calls with Kafka events  
✅ Implemented Choreography-based Saga pattern  
✅ Removed tight coupling between services  
✅ Added proper error handling and compensation  
✅ Event-driven architecture throughout  

## 🧪 Testing

Monitor Kafka topics to see events:
```bash
# Order events
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order-created

# Payment events
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment-completed

# Inventory events
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic inventory-reservation-result
```

## 📦 Project Structure

```
ecommerce/
├── api-gateway/          # API Gateway service
├── eureka/               # Service discovery
├── order-service/        # Order management (Saga orchestrator)
├── payment-service/      # Payment processing
├── product-service/      # Product catalog & inventory
├── cart-service/        # Shopping cart
├── user-service/         # User management
└── ecommerce-common/     # Shared DTOs and events
```

## 🎓 Learning Resources

This project demonstrates:
- Microservices architecture
- Event-driven architecture
- Saga pattern for distributed transactions
- Kafka message queuing
- Service discovery with Eureka
- Asynchronous processing

## 🤝 Contributing

This is a learning project. Feel free to extend it with:
- Compensation transactions
- More sophisticated error handling
- Event sourcing
- CQRS pattern
- Monitoring and observability

## 📄 License

This project is for educational purposes.

