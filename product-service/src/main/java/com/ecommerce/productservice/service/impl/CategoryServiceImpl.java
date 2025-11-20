package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.dto.CategoryRequest;
import com.ecommerce.productservice.dto.CategoryResponse;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryRequest request) {
        // Optional: Prevent duplicates
        categoryRepository.findByName(request.getName())
                .ifPresent(c -> { throw new IllegalArgumentException("Category already exists"); });

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }


    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .build())
                .toList();
    }
}
