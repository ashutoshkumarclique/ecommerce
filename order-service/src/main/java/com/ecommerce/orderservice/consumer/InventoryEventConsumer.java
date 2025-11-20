package com.ecommerce.orderservice.consumer;

import com.ecommerce.common.event.InventoryReservationResultEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "inventory-reservation-result", groupId = "order-service-group")
    public void handleInventoryReservationResult(InventoryReservationResultEvent event) {
        log.info("Received InventoryReservationResultEvent: orderId={}, status={}", 
                event.getOrderId(), event.getStatus());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));

        // Saga pattern: Update order status based on inventory reservation result
        if (event.getStatus() == InventoryReservationResultEvent.ReservationStatus.SUCCESS) {
            order.setStatus("CONFIRMED");
            log.info("Order {} status updated to CONFIRMED after inventory reservation", event.getOrderId());
        } else {
            order.setStatus("CANCELLED");
            log.warn("Order {} cancelled due to inventory reservation failure: {}", 
                    event.getOrderId(), event.getMessage());
        }

        orderRepository.save(order);
    }
}

