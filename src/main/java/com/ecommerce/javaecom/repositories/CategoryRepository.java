package com.ecommerce.javaecom.repositories;

import com.ecommerce.javaecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// <YOUR_ENTITY_NAME, TYPE OF PRIMARY KEY>
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
