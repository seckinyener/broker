package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.CustomerAlreadyExistException;
import com.seckinyener.ing.broker.model.dto.CreateCustomerDto;
import com.seckinyener.ing.broker.model.dto.CustomerDetailsDto;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.service.ICustomerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    private final AssetRepository assetRepository;

    @Transactional
    @Override
    public CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByUsername(createCustomerDto.username());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistException("Customer with username: " + createCustomerDto.username() + " already exists");
        }

        Customer customer = new Customer();
        customer.setUsername(createCustomerDto.username());
        customer.setPassword(createCustomerDto.password());
        customer.setRole(createCustomerDto.role());
        customerRepository.save(customer);

        Asset customerTRYAsset = new Asset();
        customerTRYAsset.setName("TRY");
        customerTRYAsset.setSize(new BigDecimal(createCustomerDto.tryAmount()));
        customerTRYAsset.setCustomer(customer);
        customerTRYAsset.setUsableSize(new BigDecimal(createCustomerDto.tryAmount()));
        assetRepository.save(customerTRYAsset);

        return new CustomerDetailsDto(customer.getUsername(), customer.getRole());
    }
}
