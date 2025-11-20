package com.ecommerce.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }
}


