package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.AssetNotFoundException;
import com.seckinyener.ing.broker.exception.CustomerAlreadyExistException;
import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.service.ICustomerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
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

    @Override
    public List<AssetDetailsDto> getCustomerAssets(Long customerId) {
        Customer customer = customerRepository.findCustomerById(customerId)
                .orElseThrow((() -> new CustomerNotFoundException("Customer not found with id: " + customerId)));
        return customer.getAssets().stream().sorted(Comparator.comparing(Asset::getSize).reversed()).map(item -> new AssetDetailsDto(item.getName(), item.getSize(), item.getUsableSize(), item.getUpdateDate())).toList();
    }

    @Override
    public DepositResponseDto depositMoneyForCustomer(Long customerId, DepositRequestDto depositRequestDto) {
        Asset assetTRY = assetRepository.findAssetByCustomerIdAndName(customerId, "TRY").orElseThrow(() -> new AssetNotFoundException("Asset not found with customer id: " + customerId + " and TRY"));
        assetTRY.setUsableSize(assetTRY.getUsableSize().add(depositRequestDto.amount()));
        assetTRY.setSize(assetTRY.getSize().add(depositRequestDto.amount()));
        assetRepository.save(assetTRY);
        return new DepositResponseDto(assetTRY.getSize(), assetTRY.getUsableSize());
    }
}
