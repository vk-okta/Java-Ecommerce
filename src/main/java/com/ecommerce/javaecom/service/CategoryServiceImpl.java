package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.model.Category;
import com.ecommerce.javaecom.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        // the function of the below line is just to check if the category id exists or not
        categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        // if the category exists, add the category id in category object
        category.setCategoryId(categoryId);

        // save in db
        return categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        // find the category
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        // delete it
        categoryRepository.delete(category);

        return "Category with categoryId: " + categoryId + " deleted successfully!!";
    }

}

// By annotating CategoryServiceImpl with @Service, you’re informing Spring that this class is a service component and should be managed by the Spring container.
// This means Spring will create an instance (bean) of this class and manage its lifecycle.
// Once CategoryServiceImpl is registered as a bean, it can be injected into other components (like controllers or other services) using Spring’s dependency injection.