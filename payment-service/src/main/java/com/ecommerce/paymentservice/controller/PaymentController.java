package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private Long extractUserId(String userId) {
        return Long.valueOf(userId);
    }

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<Payment> initiate(@RequestHeader("x-user-id") String userId,
                                            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.initiatePayment(extractUserId(userId), orderId));
    }

    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<Payment> confirm(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.confirmPayment(paymentId));
    }

    @PostMapping("/fail/{paymentId}")
    public ResponseEntity<Payment> fail(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.failPayment(paymentId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> get(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }
}

