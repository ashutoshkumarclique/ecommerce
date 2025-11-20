package com.ecommerce.cartservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponse {
    private Long userId;
    private List<CartItemDetails> items;
    private Double totalAmount;

    @Data
    public static class CartItemDetails {
        private Long productId;
        private String productName;
        private Double price;
        private Integer quantity;
        private Double subtotal;
    }
}
