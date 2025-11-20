package com.ecommerce.productservice.consumer;

import com.ecommerce.common.event.InventoryReservationRequestEvent;
import com.ecommerce.common.event.InventoryReservationResultEvent;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.producer.InventoryEventProducer;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final ProductRepository productRepository;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(topics = "inventory-reservation-request", groupId = "product-service-group")
    @Transactional
    public void handleInventoryReservationRequest(InventoryReservationRequestEvent event) {
        log.info("Received InventoryReservationRequestEvent for orderId: {}", event.getOrderId());

        boolean allReserved = true;
        StringBuilder failureMessage = new StringBuilder();

        // Saga pattern: Try to reserve inventory for all items
        for (InventoryReservationRequestEvent.InventoryItem item : event.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);

            if (product == null) {
                allReserved = false;
                failureMessage.append("Product ").append(item.getProductId()).append(" not found. ");
                continue;
            }

            if (product.getStock() < item.getQuantity()) {
                allReserved = false;
                failureMessage.append("Insufficient stock for product ").append(item.getProductId())
                        .append(" (available: ").append(product.getStock())
                        .append(", requested: ").append(item.getQuantity()).append("). ");
                continue;
            }

            // Reserve inventory by reducing stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            log.info("Reserved {} units of product {}", item.getQuantity(), item.getProductId());
        }

        // Publish result event
        InventoryReservationResultEvent resultEvent = InventoryReservationResultEvent.builder()
                .orderId(event.getOrderId())
                .status(allReserved 
                    ? InventoryReservationResultEvent.ReservationStatus.SUCCESS 
                    : InventoryReservationResultEvent.ReservationStatus.FAILED)
                .message(allReserved ? "Inventory reserved successfully" : failureMessage.toString())
                .processedAt(LocalDateTime.now())
                .build();

        inventoryEventProducer.publishInventoryReservationResult(resultEvent);

        if (allReserved) {
            log.info("Inventory reservation successful for orderId: {}", event.getOrderId());
        } else {
            log.warn("Inventory reservation failed for orderId: {} - {}", event.getOrderId(), failureMessage);
        }
    }
}

