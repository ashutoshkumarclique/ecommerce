package com.ecommerce.orderservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
