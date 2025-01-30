package com.ecommerce.javaecom.controller;

import com.ecommerce.javaecom.config.AppConstants;
import com.ecommerce.javaecom.payload.ProductDTO;
import com.ecommerce.javaecom.payload.ProductResponse;
import com.ecommerce.javaecom.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    // add product to a category
    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {

        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);

        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }

    // get all products
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", required = false, defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false, defaultValue = AppConstants.SORT_BY) String sortBy,
            @RequestParam(name = "sortOrder", required = false, defaultValue = AppConstants.SORT_ORDER) String sortOrder
    ) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // get products by category
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        ProductResponse productResponse = productService.searchByCategory(categoryId);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // search products by keyword
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword) {
        ProductResponse productResponse = productService.searchProductByKeyword(keyword);

        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    // update product
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(productId, productDTO);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    // delete product
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProduct = productService.deleteProduct(productId);

        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }


}
