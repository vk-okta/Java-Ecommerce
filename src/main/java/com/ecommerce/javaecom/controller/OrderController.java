package com.ecommerce.javaecom.controller;

import com.ecommerce.javaecom.payload.OrderDTO;
import com.ecommerce.javaecom.payload.OrderRequestDTO;
import com.ecommerce.javaecom.service.OrderService;
import com.ecommerce.javaecom.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final AuthUtil authUtil;
    private final OrderService orderService;

    public OrderController(AuthUtil authUtil, OrderService orderService) {
        this.authUtil = authUtil;
        this.orderService = orderService;
    }

    // place an order
    // we are not taking any order/product details in here.....the idea is during checkout
    // all the items in the logged in user's cart will be converted to an order
    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();

        OrderDTO orderDTO = orderService.placeOrder(
                emailId,
                paymentMethod,
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPg_name(),
                orderRequestDTO.getPg_id(),
                orderRequestDTO.getPg_status(),
                orderRequestDTO.getPg_response_message()
        );

        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
