package com.ecommerce.orderservice.service.impl;

import com.ecommerce.common.event.InventoryReservationRequestEvent;
import com.ecommerce.common.event.OrderCreatedEvent;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.model.*;
import com.ecommerce.orderservice.producer.OrderEventProducer;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        // Create order items from request (product details will be validated by product service)
        List<OrderItem> orderItems = request.getItems().stream().map(itemReq -> {
            return OrderItem.builder()
                    .productId(itemReq.getProductId())
                    .productName("Pending") // Will be updated when product service validates
                    .price(0.0) // Will be updated when product service validates
                    .quantity(itemReq.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        // Calculate initial total (will be validated by product service)
        double total = 0.0; // Will be recalculated after product validation

        // Create order with PENDING status - Saga pattern starts here
        Order order = Order.builder()
                .userId(userId)
                .status("PENDING")
                .totalAmount(total)
                .createdAt(LocalDateTime.now())
                .items(orderItems)
                .build();

        orderItems.forEach(i -> i.setOrder(order));
        Order savedOrder = orderRepository.save(order);

        // Publish OrderCreatedEvent - triggers payment initiation (Saga step 1)
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .userId(userId)
                .totalAmount(total)
                .createdAt(savedOrder.getCreatedAt())
                .items(orderItems.stream().map(item -> 
                    OrderCreatedEvent.OrderItemEvent.builder()
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .price(item.getPrice())
                            .quantity(item.getQuantity())
                            .build()
                ).collect(Collectors.toList()))
                .build();
        
        orderEventProducer.publishOrderCreatedEvent(orderCreatedEvent);

        // Publish InventoryReservationRequestEvent - Saga step 2
        InventoryReservationRequestEvent inventoryEvent = InventoryReservationRequestEvent.builder()
                .orderId(savedOrder.getId())
                .items(request.getItems().stream().map(itemReq ->
                    InventoryReservationRequestEvent.InventoryItem.builder()
                            .productId(itemReq.getProductId())
                            .quantity(itemReq.getQuantity())
                            .build()
                ).collect(Collectors.toList()))
                .build();
        
        orderEventProducer.publishInventoryReservationRequest(inventoryEvent);

        log.info("Order {} created and events published", savedOrder.getId());
        return mapToResponse(savedOrder);
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
}
