package com.ecommerce.javaecom.controller;

import com.ecommerce.javaecom.config.AppConstants;
import com.ecommerce.javaecom.payload.CategoryDTO;
import com.ecommerce.javaecom.payload.CategoryResponse;
import com.ecommerce.javaecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false, defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", required = false, defaultValue = AppConstants.SORT_ORDER) String sortOrder) {

        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    // Valid Annotation checks if the incoming request is meeting the constraints defined on the Model.
    // Also Valid here ensures that user do not get a 500 error when the validation on the Model level fails.
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);

        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryId, categoryDTO);

        // return new ResponseEntity<>("Category with id: " + updatedCategory.getCategoryId() + " updated Successfully!!", HttpStatus.OK);
        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        // String message = categoryService.deleteCategory(categoryId);
        CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);

        // return new ResponseEntity<>(message, HttpStatus.OK);
        return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);
    }

}
