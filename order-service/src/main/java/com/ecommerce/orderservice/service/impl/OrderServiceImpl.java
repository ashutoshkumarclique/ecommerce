package com.ecommerce.orderservice.service.impl;

import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.model.*;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Override
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        List<OrderItem> orderItems = request.getItems().stream().map(itemReq -> {
            // Fetch product details from product-service using service discovery
            ProductResponse product;
            try {
                product = restTemplate.getForObject(
                        "http://PRODUCT-SERVICE/api/products/" + itemReq.getProductId(),
                        ProductResponse.class
                );
            } catch (HttpClientErrorException.NotFound e) {
                throw new RuntimeException("Product not found with id: " + itemReq.getProductId());
            } catch (RestClientException e) {
                throw new RuntimeException("Failed to fetch product from product-service: " + e.getMessage());
            }

            if (product == null) {
                throw new RuntimeException("Product not found with id: " + itemReq.getProductId());
            }

            if (product.getName() == null || product.getPrice() == null) {
                throw new RuntimeException("Invalid product data received for product id: " + itemReq.getProductId());
            }

            return OrderItem.builder()
                    .productId(itemReq.getProductId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(itemReq.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        double total = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .userId(userId)
                .status("PLACED")
                .totalAmount(total)
                .createdAt(LocalDateTime.now())
                .items(orderItems)
                .build();

        orderItems.forEach(i -> i.setOrder(order));
        orderRepository.save(order);

        return mapToResponse(order);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    // Local inner class just for deserializing product data
    @Getter
    @Setter
    private static class ProductResponse {
        private String name;
        private Double price;
    }
}
