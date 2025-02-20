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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
        newCartItem.setProductPrice(product.getPrice());
        newCartItem.setProductSpecialPrice(product.getSpecialPrice());

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

        // if cartId is -1, the cart is not created yet, create a new cart for this user
        Cart cart = cartId == -1 ? cart = createCart() : cartRepository.findCartByEmailAndCartId(email, cartId);

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

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        // for delete the value of quantity will be -1 else 1

        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Long userCartId = userCart.getCartId();

        Cart cart = cartRepository.findById(userCartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", userCartId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getQuantity() == 0) {
            throw new APIExceptions("Product " + product.getProductName() + " is not available in the stock");
        }

        if (product.getQuantity() < quantity) {
            throw new APIExceptions("Only " + product.getQuantity() + product.getProductName() + " available in stock");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem == null) {
            // TODO: if product is not in cart and user tries to add something, add the product in the cart
            throw new APIExceptions("Product " + product.getProductName() + " is not available in the cart");
        }

        int newQuantity = quantity + cartItem.getQuantity();

        if (newQuantity < 0) {
            throw new APIExceptions("Cart is already empty!!");
        }

        if (newQuantity == 0) {
            deleteProductFromCart(userCartId, productId);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());

            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        if (updatedCartItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIExceptions("Product " + productId + " is not available in the cart");
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductSpecialPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product has been removed from the cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        // update a particular product in this cart

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);

        if (cartItem == null) {
            throw new APIExceptions("Product " + productId + " is not available in the cart");
        }

        cartItem.setDiscount(product.getDiscount());
        cartItem.setProductPrice(product.getPrice());
        cartItem.setProductSpecialPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartItem.getProductSpecialPrice() * cartItem.getQuantity());

        cartItemRepository.save(cartItem);
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
