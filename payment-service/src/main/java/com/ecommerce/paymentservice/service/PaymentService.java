package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.client.OrderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    public Payment initiatePayment(Long userId, Long orderId) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setStatus(Payment.Status.PENDING);
        return paymentRepository.save(payment);
    }

    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(Payment.Status.SUCCESS);
        paymentRepository.save(payment);

        orderClient.updateOrderStatus(payment.getOrderId(), "PAID");
        return payment;
    }

    public Payment failPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(Payment.Status.FAILED);
        paymentRepository.save(payment);

        orderClient.updateOrderStatus(payment.getOrderId(), "PAYMENT_FAILED");
        return payment;
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}

