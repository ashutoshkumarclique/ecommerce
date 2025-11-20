package com.ecommerce.orderservice.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
