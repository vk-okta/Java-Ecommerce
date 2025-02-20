package com.ecommerce.javaecom.controller;

import com.ecommerce.javaecom.model.Cart;
import com.ecommerce.javaecom.payload.CartDTO;
import com.ecommerce.javaecom.payload.CartResponse;
import com.ecommerce.javaecom.repositories.CartRepository;
import com.ecommerce.javaecom.service.CartService;
import com.ecommerce.javaecom.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final AuthUtil authUtil;
    private final CartRepository cartRepository;

    public CartController(CartService cartService, AuthUtil authUtil, CartRepository cartRepository) {
        this.cartService = cartService;
        this.authUtil = authUtil;
        this.cartRepository = cartRepository;
    }

    // add product to the logged in user cart
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<CartResponse> getAllCarts() {
        CartResponse cartResponse = cartService.getAllCarts();
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    // get the cart of logged in user
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartByUserId() {
        String email = authUtil.loggedInEmail();

        Cart cart = cartRepository.findCartByEmail(email);
        Long cartId = cart != null ? cart.getCartId() : -1;

        CartDTO cartDTO = cartService.getCart(email, cartId);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    // update quantity in my cart
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
