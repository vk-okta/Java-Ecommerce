package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.payload.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
