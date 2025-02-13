package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.payload.CartDTO;
import com.ecommerce.javaecom.payload.CartResponse;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    CartResponse getAllCarts();

    CartDTO getCart(String email, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer operation);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
