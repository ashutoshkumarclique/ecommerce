package com.ecommerce.orderservice.producer;

import com.ecommerce.common.event.InventoryReservationRequestEvent;
import com.ecommerce.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for orderId: {}", event.getOrderId());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("order-created", event.getOrderId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("OrderCreatedEvent published successfully for orderId: {}", event.getOrderId());
            } else {
                log.error("Failed to publish OrderCreatedEvent for orderId: {}", event.getOrderId(), ex);
            }
        });
    }

    public void publishInventoryReservationRequest(InventoryReservationRequestEvent event) {
        log.info("Publishing InventoryReservationRequestEvent for orderId: {}", event.getOrderId());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("inventory-reservation-request", event.getOrderId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("InventoryReservationRequestEvent published successfully for orderId: {}", event.getOrderId());
            } else {
                log.error("Failed to publish InventoryReservationRequestEvent for orderId: {}", event.getOrderId(), ex);
            }
        });
    }
}

