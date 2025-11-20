package com.ecommerce.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime completedAt;
    
    public enum PaymentStatus {
        SUCCESS,
        FAILED
    }
}

