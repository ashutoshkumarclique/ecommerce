package com.ecommerce.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

    @PutMapping("/api/orders/{orderId}/status/{status}")
    void updateOrderStatus(@PathVariable Long orderId, @PathVariable String status);
}

