package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.model.User;
import com.ecommerce.javaecom.payload.AddressDTO;
import com.ecommerce.javaecom.payload.AddressResponse;
import jakarta.validation.Valid;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
    AddressResponse getAllAddress();
    AddressDTO getAddressById(Long addressId);
    AddressResponse getUserAddress(User user);
    AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO);
    String deleteAddressById(Long addressId);
}
