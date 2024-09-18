package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.CustomerAlreadyExistException;
import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.service.ICustomerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    private final AssetService assetService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto) {
        validateCustomerIsNotExist(createCustomerDto);

        Customer customer = createAndSaveCustomer(createCustomerDto);

        assetService.createAssetForCustomer(new BigDecimal(createCustomerDto.tryAmount()), customer);

        return new CustomerDetailsDto(customer.getUsername(), customer.getRole());
    }

    private void validateCustomerIsNotExist(CreateCustomerDto createCustomerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByUsername(createCustomerDto.username());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistException("Customer with username: " + createCustomerDto.username() + " already exists");
        }
    }

    private Customer createAndSaveCustomer(CreateCustomerDto createCustomerDto) {
        Customer customer = new Customer();
        customer.setUsername(createCustomerDto.username());
        customer.setPassword(passwordEncoder.encode(createCustomerDto.password()));
        customer.setRole(createCustomerDto.role());
        customerRepository.save(customer);
        return customer;
    }

    @Override
    public List<AssetDetailsDto> getCustomerAssets(Long customerId) {
        Customer customer = findCustomerById(customerId);
        return customer.getAssets().stream().sorted(Comparator.comparing(Asset::getSize).reversed()).map(item -> new AssetDetailsDto(item.getName(), item.getSize(), item.getUsableSize(), item.getUpdateDate())).toList();
    }

    @Override
    public Customer findCustomerById(Long customerId) {
        return customerRepository.findCustomerById(customerId)
                .orElseThrow((() -> new CustomerNotFoundException("Customer not found with id: " + customerId)));
    }
}
