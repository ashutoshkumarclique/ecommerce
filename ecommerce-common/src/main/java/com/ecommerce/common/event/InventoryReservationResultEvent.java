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
public class InventoryReservationResultEvent {
    private Long orderId;
    private ReservationStatus status;
    private String message;
    private LocalDateTime processedAt;
    
    public enum ReservationStatus {
        SUCCESS,
        FAILED
    }
}

