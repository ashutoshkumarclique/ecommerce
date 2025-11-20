# Kafka Setup Guide

## Prerequisites

This project requires Apache Kafka to be running. Follow these steps to set up Kafka:

## Installation

### Option 1: Using Docker (Recommended)

```bash
# Start Kafka and Zookeeper using Docker Compose
docker-compose up -d

# Or manually:
docker run -d --name zookeeper -p 2181:2181 zookeeper:latest
docker run -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID=1 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:latest
```

### Option 2: Manual Installation

1. Download Kafka from https://kafka.apache.org/downloads
2. Extract the archive
3. Start Zookeeper:
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
4. Start Kafka:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```

## Verify Kafka is Running

```bash
# List topics (should be empty initially, topics are auto-created)
kafka-topics.sh --bootstrap-server localhost:9092 --list

# After starting services, you should see:
# - order-created
# - payment-completed
# - inventory-reservation-request
# - inventory-reservation-result
```

## Configuration

All services are configured to connect to Kafka at `localhost:9092`. This can be changed in each service's `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

## Troubleshooting

1. **Connection Refused**: Make sure Kafka is running on port 9092
2. **Topics Not Created**: Topics are auto-created when first message is published
3. **Consumer Not Receiving Messages**: Check consumer group ID and topic name

