package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long userId, OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByUser(Long userId);
    void updateOrderStatus(Long orderId, String status);
}
