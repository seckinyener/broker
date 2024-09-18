package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.CustomerAlreadyExistException;
import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.exception.UsableSizeIsNotSufficientForWithdrawException;
import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.repository.AssetRepository;
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

    private final AssetRepository assetRepository;

    private final AssetService assetService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByUsername(createCustomerDto.username());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistException("Customer with username: " + createCustomerDto.username() + " already exists");
        }

        Customer customer = new Customer();
        customer.setUsername(createCustomerDto.username());
        customer.setPassword(passwordEncoder.encode(createCustomerDto.password()));
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
        Customer customer = findCustomerById(customerId);
        return customer.getAssets().stream().sorted(Comparator.comparing(Asset::getSize).reversed()).map(item -> new AssetDetailsDto(item.getName(), item.getSize(), item.getUsableSize(), item.getUpdateDate())).toList();
    }

    @Transactional
    @Override
    public DepositResponseDto depositMoneyForCustomer(Long customerId, DepositRequestDto depositRequestDto) {
        Asset assetTRY = assetService.findAssetByCustomerIdAndName(customerId, "TRY");
        assetTRY.setUsableSize(assetTRY.getUsableSize().add(depositRequestDto.amount()));
        assetTRY.setSize(assetTRY.getSize().add(depositRequestDto.amount()));
        assetRepository.save(assetTRY);
        return new DepositResponseDto(assetTRY.getSize(), assetTRY.getUsableSize());
    }

    @Transactional
    @Override
    public WithdrawResponseDto withdrawMoneyForCustomer(Long customerId, WithdrawRequestDto withdrawRequestDto) {
        Asset assetTRY = assetService.findAssetByCustomerIdAndName(customerId, "TRY");
        if (assetTRY.getUsableSize().compareTo(withdrawRequestDto.amount()) < 0) {
            throw new UsableSizeIsNotSufficientForWithdrawException("Usable size is not sufficient to transfer this amount to iban");
        }
        // We can think to send this amount to the iban number which is provided in request dto.
        assetTRY.setSize(assetTRY.getSize().subtract(withdrawRequestDto.amount()));
        assetTRY.setUsableSize(assetTRY.getUsableSize().subtract(withdrawRequestDto.amount()));
        assetRepository.save(assetTRY);
        return new WithdrawResponseDto(assetTRY.getSize(), assetTRY.getUsableSize(), withdrawRequestDto.iban());
    }

    @Override
    public Customer findCustomerById(Long customerId) {
        return customerRepository.findCustomerById(customerId)
                .orElseThrow((() -> new CustomerNotFoundException("Customer not found with id: " + customerId)));
    }
}
