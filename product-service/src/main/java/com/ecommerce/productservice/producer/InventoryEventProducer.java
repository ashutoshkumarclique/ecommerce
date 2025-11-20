package com.ecommerce.productservice.producer;

import com.ecommerce.common.event.InventoryReservationResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryReservationResult(InventoryReservationResultEvent event) {
        log.info("Publishing InventoryReservationResultEvent for orderId: {}, status: {}", 
                event.getOrderId(), event.getStatus());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                "inventory-reservation-result", 
                event.getOrderId().toString(), 
                event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("InventoryReservationResultEvent published successfully for orderId: {}", event.getOrderId());
            } else {
                log.error("Failed to publish InventoryReservationResultEvent for orderId: {}", event.getOrderId(), ex);
            }
        });
    }
}

