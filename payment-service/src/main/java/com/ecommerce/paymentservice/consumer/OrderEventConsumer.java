package com.ecommerce.paymentservice.consumer;

import com.ecommerce.common.event.OrderCreatedEvent;
import com.ecommerce.common.event.PaymentInitiatedEvent;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.producer.PaymentEventProducer;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: orderId={}, userId={}, totalAmount={}", 
                event.getOrderId(), event.getUserId(), event.getTotalAmount());

        // Saga pattern: Automatically initiate payment when order is created
        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setUserId(event.getUserId());
        payment.setStatus(Payment.Status.PENDING);
        Payment savedPayment = paymentRepository.save(payment);

        // Publish PaymentInitiatedEvent
        PaymentInitiatedEvent paymentInitiatedEvent = PaymentInitiatedEvent.builder()
                .paymentId(savedPayment.getId())
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .createdAt(LocalDateTime.now())
                .build();

        paymentEventProducer.publishPaymentInitiated(paymentInitiatedEvent);

        // Simulate payment processing (in real scenario, this would be async)
        // For demo purposes, we'll auto-process after a short delay
        processPayment(savedPayment.getId(), event.getTotalAmount());
    }

    private void processPayment(Long paymentId, Double amount) {
        // Simulate payment processing - in real scenario, this would be handled by payment gateway
        // For demo: auto-approve payments (you can modify this to randomly fail for testing)
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        // Simulate payment success (90% success rate for demo)
        boolean success = Math.random() > 0.1;
        
        if (success) {
            payment.setStatus(Payment.Status.SUCCESS);
            paymentRepository.save(payment);

            com.ecommerce.common.event.PaymentCompletedEvent paymentCompletedEvent = 
                com.ecommerce.common.event.PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId())
                    .amount(amount)
                    .status(com.ecommerce.common.event.PaymentCompletedEvent.PaymentStatus.SUCCESS)
                    .completedAt(LocalDateTime.now())
                    .build();

            paymentEventProducer.publishPaymentCompleted(paymentCompletedEvent);
            log.info("Payment {} processed successfully", paymentId);
        } else {
            payment.setStatus(Payment.Status.FAILED);
            paymentRepository.save(payment);

            com.ecommerce.common.event.PaymentCompletedEvent paymentCompletedEvent = 
                com.ecommerce.common.event.PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId())
                    .amount(amount)
                    .status(com.ecommerce.common.event.PaymentCompletedEvent.PaymentStatus.FAILED)
                    .completedAt(LocalDateTime.now())
                    .build();

            paymentEventProducer.publishPaymentCompleted(paymentCompletedEvent);
            log.warn("Payment {} failed", paymentId);
        }
    }
}

