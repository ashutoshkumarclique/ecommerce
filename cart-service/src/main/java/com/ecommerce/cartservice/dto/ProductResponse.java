package com.ecommerce.cartservice.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
}
