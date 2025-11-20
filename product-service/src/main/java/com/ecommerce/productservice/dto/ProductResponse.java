package com.ecommerce.productservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String description;
    private Boolean active;
    private String categoryName;
}
