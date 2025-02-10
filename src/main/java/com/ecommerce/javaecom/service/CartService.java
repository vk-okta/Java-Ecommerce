package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.payload.CartDTO;
import com.ecommerce.javaecom.payload.CartResponse;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    CartResponse getAllCarts();
}
