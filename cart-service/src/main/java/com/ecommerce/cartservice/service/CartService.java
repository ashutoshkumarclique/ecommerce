package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    void addItem(Long userId, Long productId);
    void removeItem(Long userId, Long productId);
    void clearCart(Long userId);
}
