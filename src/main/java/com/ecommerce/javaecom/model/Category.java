package com.ecommerce.javaecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// this annotation marks this Java class as an entity that should be mapped to a table in the database.
// the name of the table will be same as class name
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(min = 2, message = "must contain atleast 2 characters")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    // TODO
    // find out why it is a good practice to have
    // a default constructor in the Entity
    // public Category() {
    //}

    // all the constructors, getters,setters will be added by lombok

    // public Category(Long categoryId, String categoryName) {
    //    this.categoryId = categoryId;
    //    this.categoryName = categoryName;
    //}

    // Getters and setters are used to set the value in db
    // and that fetch the value from db

    // public String getCategoryName() {
    //    return categoryName;
    //}

    // public void setCategoryName(String categoryName) {
    //    this.categoryName = categoryName;
    //}

    // public Long getCategoryId() {
    //    return categoryId;
    //}

    // public void setCategoryId(Long categoryId) {
    //    this.categoryId = categoryId;
    //}
}
