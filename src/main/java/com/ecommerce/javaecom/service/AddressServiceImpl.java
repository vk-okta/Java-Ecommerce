package com.ecommerce.javaecom.service;

import com.ecommerce.javaecom.exceptions.ResourceNotFoundException;
import com.ecommerce.javaecom.model.Address;
import com.ecommerce.javaecom.model.User;
import com.ecommerce.javaecom.payload.AddressDTO;
import com.ecommerce.javaecom.payload.AddressResponse;
import com.ecommerce.javaecom.repositories.AddressRepository;
import com.ecommerce.javaecom.repositories.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(ModelMapper modelMapper, AddressRepository addressRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        // get the existing list of address for this user
        List<Address> addressList = user.getAddresses();

        // add the new address
        addressList.add(address);

        // update the user with the new list
        user.setAddresses(addressList);

        // User and Address have Bi-directional relationship
        // updating the other side of relationship
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressResponse getAllAddress() {
        List<Address> addressList = addressRepository.findAll();

        List<AddressDTO> addressDTOS = addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setContent(addressDTOS);

        return addressResponse;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("address", "id", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressResponse getUserAddress(User user) {
        List<Address> addressList = user.getAddresses();

        List<AddressDTO> addressDTOS = addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setContent(addressDTOS);

        return addressResponse;
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO) {

        // get address from DB
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("address", "id", addressId));

        Address address = modelMapper.map(addressDTO, Address.class);

        existingAddress.setCountry(address.getCountry());
        existingAddress.setState(address.getState());
        existingAddress.setCity(address.getCity());
        existingAddress.setStreet(address.getStreet());
        existingAddress.setBuildingName(address.getBuildingName());
        existingAddress.setPinCode(address.getPinCode());

        // this saves the updated address in DB
        Address updatedAddress = addressRepository.save(existingAddress);

        // for the user, update the existing address

        // get the user who has this address
        User user = existingAddress.getUser();

        // remove the address which is to be updated
        user.getAddresses().removeIf(userExistingAddress -> userExistingAddress.getAddressId().equals(addressId));

        // insert the new updated address
        user.getAddresses().add(updatedAddress);

        // save the user
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        // get address from DB
        Address existingAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("address", "id", addressId));

        // get the user who has this address
        User user = existingAddress.getUser();
        // remove the address
        user.getAddresses().removeIf(userExistingAddress -> userExistingAddress.getAddressId().equals(addressId));
        // save the user
        userRepository.save(user);

        addressRepository.delete(existingAddress);

        return "Address successfully deleted with id: " + addressId;
    }
}
