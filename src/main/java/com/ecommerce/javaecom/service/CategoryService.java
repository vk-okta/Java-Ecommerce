package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    void createCategory(Category category);

    String deleteCategory(Long categoryId);

    Category updateCategory(Long categoryId, Category category);
}
