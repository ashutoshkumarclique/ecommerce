package com.ecommerce.orderservice.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
}
