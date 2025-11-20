package com.ecommerce.orderservice.consumer;

import com.ecommerce.common.event.PaymentCompletedEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-completed", groupId = "order-service-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent: paymentId={}, orderId={}, status={}", 
                event.getPaymentId(), event.getOrderId(), event.getStatus());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));

        // Saga pattern: Update order status based on payment result
        if (event.getStatus() == PaymentCompletedEvent.PaymentStatus.SUCCESS) {
            order.setStatus("PAID");
            log.info("Order {} status updated to PAID", event.getOrderId());
        } else {
            order.setStatus("PAYMENT_FAILED");
            log.info("Order {} status updated to PAYMENT_FAILED", event.getOrderId());
        }

        orderRepository.save(order);
    }
}

