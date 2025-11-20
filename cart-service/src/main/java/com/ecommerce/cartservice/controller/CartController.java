package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private Long extractUserId(String userIdHeader) {
        return Long.valueOf(userIdHeader);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(cartService.getCart(extractUserId(userId)));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addItem(@RequestHeader("x-user-id") String userId,
                                          @PathVariable Long productId) {
        cartService.addItem(extractUserId(userId), productId);
        return ResponseEntity.ok("Item added to cart");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItem(@RequestHeader("x-user-id") String userId,
                                             @PathVariable Long productId) {
        cartService.removeItem(extractUserId(userId), productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("x-user-id") String userId) {
        cartService.clearCart(extractUserId(userId));
        return ResponseEntity.ok("Cart cleared");
    }
}
