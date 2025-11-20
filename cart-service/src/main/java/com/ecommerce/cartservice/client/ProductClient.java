package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE") // matches application.name in product service
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable Long id);
}
