package com.ecommerce.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    private String name;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "Stock cannot be null")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
