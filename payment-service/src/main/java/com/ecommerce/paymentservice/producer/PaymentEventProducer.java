package com.ecommerce.paymentservice.producer;

import com.ecommerce.common.event.PaymentCompletedEvent;
import com.ecommerce.common.event.PaymentInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Publishing PaymentInitiatedEvent for paymentId: {}", event.getPaymentId());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("payment-initiated", event.getPaymentId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("PaymentInitiatedEvent published successfully for paymentId: {}", event.getPaymentId());
            } else {
                log.error("Failed to publish PaymentInitiatedEvent for paymentId: {}", event.getPaymentId(), ex);
            }
        });
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Publishing PaymentCompletedEvent for paymentId: {}, status: {}", event.getPaymentId(), event.getStatus());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("payment-completed", event.getPaymentId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("PaymentCompletedEvent published successfully for paymentId: {}", event.getPaymentId());
            } else {
                log.error("Failed to publish PaymentCompletedEvent for paymentId: {}", event.getPaymentId(), ex);
            }
        });
    }
}

