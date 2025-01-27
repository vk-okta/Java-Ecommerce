package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.payload.CategoryDTO;
import com.ecommerce.javaecom.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories();

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO);
}
