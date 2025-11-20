package com.ecommerce.productservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @NotNull(message = "Stock cannot be null")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
