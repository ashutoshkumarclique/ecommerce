package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryRequest;
import com.ecommerce.productservice.dto.CategoryResponse;
import com.ecommerce.productservice.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryRequest request);
    Category updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
}
