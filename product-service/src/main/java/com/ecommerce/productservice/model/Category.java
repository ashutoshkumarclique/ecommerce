package com.ecommerce.productservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 250, message = "Description cannot exceed 250 characters")
    private String description;

    // Bidirectional mapping (optional, useful for viewing products per category)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Product> products;
}
