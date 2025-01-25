package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.exceptions.APIExceptions;
import com.ecommerce.javaecom.exceptions.ResourceNotFoundException;
import com.ecommerce.javaecom.model.Category;
import com.ecommerce.javaecom.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // if (categories.isEmpty()) {
        //     throw new APIExceptions("No Categories created Yet!!!");
        // }

        return categories;
    }

    @Override
    public void createCategory(Category category) {
        // check if a category with the same name exists or not
        // we need to write a custom method to check this
        Category findCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if (findCategory != null) {
            throw new APIExceptions("Category with the name " + category.getCategoryName() + " already exists!!!");
        }

        categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        // the function of the below line is just to check if the category id exists or not
        categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // if the category exists, add the category id in category object
        category.setCategoryId(categoryId);

        // save in db
        return categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        // using custom exception handler
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // delete it
        categoryRepository.delete(category);

        return "Category with categoryId: " + categoryId + " deleted successfully!!";
    }

}

// By annotating CategoryServiceImpl with @Service, you’re informing Spring that this class is a service component and should be managed by the Spring container.
// This means Spring will create an instance (bean) of this class and manage its lifecycle.
// Once CategoryServiceImpl is registered as a bean, it can be injected into other components (like controllers or other services) using Spring’s dependency injection.