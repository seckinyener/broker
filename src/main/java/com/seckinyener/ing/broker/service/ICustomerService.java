package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.*;
import com.seckinyener.ing.broker.model.entity.Customer;

import java.util.List;

public interface ICustomerService {

    CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto);

    List<AssetDetailsDto> getCustomerAssets(Long customerId);

    DepositResponseDto depositMoneyForCustomer(Long customerId, DepositRequestDto depositRequestDto);

    WithdrawResponseDto withdrawMoneyForCustomer(Long customerId, WithdrawRequestDto withdrawRequestDto);

    Customer findCustomerById(Long customerId);
}
