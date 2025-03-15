package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.exceptions.APIExceptions;
import com.ecommerce.javaecom.exceptions.ResourceNotFoundException;
import com.ecommerce.javaecom.model.*;
import com.ecommerce.javaecom.payload.OrderDTO;
import com.ecommerce.javaecom.payload.OrderItemDTO;
import com.ecommerce.javaecom.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public OrderServiceImpl(CartRepository cartRepository, AddressRepository addressRepository, PaymentRepository paymentRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartService cartService, ModelMapper modelMapper, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, String paymentMethod, Long addressId, String pgName, String pgId, String pgStatus, String pgResponseMessage) {

        // get the user's cart, the cart will be turned into the order
        // for now we have just one cart per user hence finding by email
        // if multiple cart per user leverage findCartByEmailAndCartId
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        // get the delivery address
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // create the order object
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!!");
        order.setAddress(address);

        // create the payment object
        Payment payment = new Payment(pgId, paymentMethod, pgName, pgResponseMessage, pgStatus);
        payment.setOrder(order);

        // save the payment details
        // we need to associate payment with order before order can be saved
        payment = paymentRepository.save(payment);

        // set the payment details in order
        order.setPayment(payment);

        // save the order
        Order savedOrder = orderRepository.save(order);

        // get all the items in cart
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIExceptions("Cart is Empty!!");
        }

        // Create the List Order items objects: each order item is a cart item
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductSpecialPrice());
            // associate this order item with the order
            orderItem.setOrder(order);
            // add this item in the list
            orderItems.add(orderItem);
        }

        // save the order items
        orderItems = orderItemRepository.saveAll(orderItems);

        // for every item in cart, reduce the stock quantity and
        // since the order is placed
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // reduce the stock quantity
            product.setQuantity(product.getQuantity() - quantity);
            // update the product in DB
            productRepository.save(product);

            // remove this item from cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        // orderDTO has a list of OrderItems, convert all those OrderItems to its DTO
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
