package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long extractUserId(String userIdHeader) {
        return Long.valueOf(userIdHeader);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("x-user-id") String userId,
                                                     @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(extractUserId(userId), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
                                                   @PathVariable String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
