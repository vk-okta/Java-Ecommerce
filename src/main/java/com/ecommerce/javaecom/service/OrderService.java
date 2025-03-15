package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId, String paymentMethod, Long addressId, String pgName, String pgId, String pgStatus, String pgResponseMessage);
}
