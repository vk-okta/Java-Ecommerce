package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.exceptions.APIExceptions;
import com.ecommerce.javaecom.exceptions.ResourceNotFoundException;
import com.ecommerce.javaecom.model.Cart;
import com.ecommerce.javaecom.model.CartItem;
import com.ecommerce.javaecom.model.Product;
import com.ecommerce.javaecom.payload.CartDTO;
import com.ecommerce.javaecom.payload.CartResponse;
import com.ecommerce.javaecom.payload.ProductDTO;
import com.ecommerce.javaecom.repositories.CartItemRepository;
import com.ecommerce.javaecom.repositories.CartRepository;
import com.ecommerce.javaecom.repositories.ProductRepository;
import com.ecommerce.javaecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, ModelMapper modelMapper, AuthUtil authUtil) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
        this.authUtil = authUtil;
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // find existing cart or create one for the logged in user
        Cart cart = createCart();

        // retrieve product details
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // perform validations

        // check if there is existing product in cart
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIExceptions("Product " + product.getProductName() + " already exists in cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIExceptions(product.getProductName() + "is not available in the stock");
        }

        if (product.getQuantity() < quantity) {
            throw new APIExceptions("Only " + product.getQuantity() + product.getProductName() + " available in stock");
        }

        // create cart item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // save cart item
        cartItemRepository.save(newCartItem);

        // TODO: why this is needed when there is a new cart but not when for existing cart
        cart.getCartItems().add(newCartItem);

        // return the updated cart
        // product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // change all products in cart to ProductDTO
        List<ProductDTO> productDTOList = cart.getCartItems()
                                              .stream()
                                              .map(item -> {
                                                       ProductDTO map = modelMapper.map(item, ProductDTO.class);
                                                       // don't take the product quantity in stock, take the quantity in cart
                                                       map.setQuantity(item.getQuantity());
                                                       return map;
                                                   }
                                              ).toList();

        // add List of ProductDTOs in CartDTO
        cartDTO.setProducts(productDTOList);

        return cartDTO;
    }

    @Override
    public CartResponse getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        // convert cart to CartDTO
        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            // for every cart, convert the products in cart items to product DTO
            List<ProductDTO> productDTOS = cart.getCartItems()
                                               .stream()
                                               .map(cartItem -> modelMapper.map(cartItem, ProductDTO.class))
                                               .toList();

            cartDTO.setProducts(productDTOS);

            return cartDTO;
        }).toList();

        CartResponse cartResponse = new CartResponse();
        cartResponse.setContent(cartDTOS);

        return cartResponse;
    }

    @Override
    public CartDTO getCart(String email, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(email, cartId);

        // if you leave it like this the products [] will be empty, hence need to add cart DTo
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // cart.getCartItems().forEach(cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity()));

        List<ProductDTO> productDTOS = cart.getCartItems()
                                           .stream()
                                           .map(cartItem -> modelMapper.map(cartItem, ProductDTO.class))
                                           .toList();

        cartDTO.setProducts(productDTOS);

        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if (userCart != null) {
            return userCart;
        }

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());

        return cartRepository.save(newCart);
    }

}
