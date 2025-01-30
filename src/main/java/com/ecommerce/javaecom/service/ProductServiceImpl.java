package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.exceptions.ResourceNotFoundException;
import com.ecommerce.javaecom.model.Category;
import com.ecommerce.javaecom.model.Product;
import com.ecommerce.javaecom.payload.ProductDTO;
import com.ecommerce.javaecom.payload.ProductResponse;
import com.ecommerce.javaecom.repositories.CategoryRepository;
import com.ecommerce.javaecom.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        // map productDTO to class
        Product product = modelMapper.map(productDTO, Product.class);

        Category findCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        product.setCategory(findCategory);
        product.setImage("default.png");

        Double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * productDTO.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        // Page<Product> productPage = productRepository.findAll(pageDetails);
        //
        // List<Product> products = productPage.getContent();
        List<Product> products = productRepository.findAll();

        // map all the products into DTOs
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        // productResponse.setPageNumber(productPage.getNumber());
        // productResponse.setPageSize(productPage.getSize());
        // productResponse.setTotalPages(productPage.getTotalPages());
        // productResponse.setTotalElements(productPage.getTotalElements());
        // productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {

        // % to match any length of char before and after keyword
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        // get the existing product from db
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // update the product info with the details from request body
        existingProduct.setProductName(productDTO.getProductName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setQuantity(productDTO.getQuantity());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDiscount(productDTO.getDiscount());

        Double specialPrice = productDTO.getPrice() - ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

        // save in DB
        Product updatedProduct = productRepository.save(existingProduct);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(existingProduct);

        return modelMapper.map(existingProduct, ProductDTO.class);
    }
}
