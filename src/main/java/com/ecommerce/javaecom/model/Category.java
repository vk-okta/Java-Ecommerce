package com.ecommerce.javaecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String categoryName;

    // TODO
    // find out why it is a good practice to have
    // a default constructor in the Entity
    //public Category() {
    //}

    // all the constructors, getters,setters will be added by lombok

    //public Category(Long categoryId, String categoryName) {
    //    this.categoryId = categoryId;
    //    this.categoryName = categoryName;
    //}

    // Getters and setters are used to set the value in db
    // and that fetch the value from db

    //public String getCategoryName() {
    //    return categoryName;
    //}

    //public void setCategoryName(String categoryName) {
    //    this.categoryName = categoryName;
    //}

    //public Long getCategoryId() {
    //    return categoryId;
    //}

    //public void setCategoryId(Long categoryId) {
    //    this.categoryId = categoryId;
    //}
}
