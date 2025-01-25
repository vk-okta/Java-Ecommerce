package com.ecommerce.javaecom.repositories;

import com.ecommerce.javaecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// <YOUR_ENTITY_NAME, TYPE OF PRIMARY KEY>
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // How will this work without us writing SQL queries?
    // JPA will check the  name of the method
    // findBy -> tells that this will be a findBy query
    // CategoryName -> this will tell JPA the name of the field to find
    // the convention is that the name should be the camelCase of field name
    // categoryName --> CategoryName
    Category findByCategoryName(String categoryName);
}
