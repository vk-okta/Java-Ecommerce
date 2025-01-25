package com.ecommerce.javaecom.controller;

import com.ecommerce.javaecom.model.Category;
import com.ecommerce.javaecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api") // all the endpoints in this class will have /api as the predecessor
public class CategoryController {

    private CategoryService categoryService;

    // When Spring encounters the @Autowired annotation, it searches the application context for a bean that matches the type.
    // In this case, it will look for a bean that implements the CategoryService interface and inject the CategoryServiceImpl bean if it finds it.
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    // @RequestMapping(value = "/public/categories", method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Valid Annotation checks if the incoming request is meeting the constraints defined on the Model.
    // Also Valid here ensures that user do not get a 500 error when the validation on the Model level fails.
    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@Valid @RequestBody Category category) {
        categoryService.createCategory(category);

        return new ResponseEntity<>("Category created successfully!!", HttpStatus.CREATED);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody Category category, @PathVariable Long categoryId) {
        Category updatedCategory = categoryService.updateCategory(categoryId, category);

        return new ResponseEntity<>("Category with id: " + updatedCategory.getCategoryId() + " updated Successfully!!", HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        String message = categoryService.deleteCategory(categoryId);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
