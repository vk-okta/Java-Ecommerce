package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.exceptions.APIExceptions;
import com.ecommerce.javaecom.exceptions.ResourceNotFoundException;
import com.ecommerce.javaecom.model.Category;
import com.ecommerce.javaecom.payload.CategoryDTO;
import com.ecommerce.javaecom.payload.CategoryResponse;
import com.ecommerce.javaecom.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

        // if (categories.isEmpty()) {
        //     throw new APIExceptions("No Categories created Yet!!!");
        // }

        // we are mapping the category Model to category DTO
        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // map CategoryDTO -> category class
        Category category = modelMapper.map(categoryDTO, Category.class);

        // check if a category with the same name exists or not
        // we need to write a custom method to check this
        Category findCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if (findCategory != null) {
            throw new APIExceptions("Category with the name " + category.getCategoryName() + " already exists!!!");
        }

        Category savedCategory = categoryRepository.save(category);

        // since save method returns a category class, we need to map it to the DTO class first and then return it
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        // the function of the below line is just to check if the category id exists or not
        categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        /*
         // check if similar category exists by name in the category
         // to add this is doubt, since to check we need to fetch all categories
         // putting a potential performance issue
         boolean isNamePresent = false;
         List<Category> categoriesInDB = categoryRepository.findAll();
         for (Category categoryInDB : categoriesInDB) {
             if (categoryInDB.getCategoryName().equals(category.getCategoryName()) && !categoryInDB.getCategoryId().equals(categoryId)) {
                 isNamePresent = true;
             }
         }

         if (isNamePresent) {
             throw new APIExceptions("Category with the name " + category.getCategoryName() + " already exists!!!");
         }
         if the category exists, add the category id in category object
        */

        category.setCategoryId(categoryId);

        // save in db
        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        // using custom exception handler
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // delete it
        categoryRepository.delete(category);

        // return "Category with categoryId: " + categoryId + " deleted successfully!!";
        // since the delete method doesn't return anything.....return the category that was found to be deleted
        return modelMapper.map(category, CategoryDTO.class);
    }

}

// By annotating CategoryServiceImpl with @Service, you’re informing Spring that this class is a service component and should be managed by the Spring container.
// This means Spring will create an instance (bean) of this class and manage its lifecycle.
// Once CategoryServiceImpl is registered as a bean, it can be injected into other components (like controllers or other services) using Spring’s dependency injection.