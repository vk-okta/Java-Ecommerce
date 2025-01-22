package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private List<Category> categories = new ArrayList<>();
    private Long categoryId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(categoryId++);
        categories.add(category);
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        Optional<Category> optionalCategory = categories.stream()
                                      .filter(e -> e.getCategoryId().equals(categoryId))
                                      .findFirst();
        // if category is present
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            return existingCategory;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream()
                                      .filter(e -> e.getCategoryId().equals(categoryId))
                                      .findFirst()
                                      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found"));

        categories.remove(category);

        return "Category with categoryId: " + categoryId + " deleted successfully!!";
    }

}

// By annotating CategoryServiceImpl with @Service, you’re informing Spring that this class is a service component and should be managed by the Spring container.
// This means Spring will create an instance (bean) of this class and manage its lifecycle.
// Once CategoryServiceImpl is registered as a bean, it can be injected into other components (like controllers or other services) using Spring’s dependency injection.