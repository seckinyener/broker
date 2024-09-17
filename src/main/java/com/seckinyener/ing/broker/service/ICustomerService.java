package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.*;

import java.util.List;

public interface ICustomerService {

    CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto);

    List<AssetDetailsDto> getCustomerAssets(Long customerId);

    DepositResponseDto depositMoneyForCustomer(Long customerId, DepositRequestDto depositRequestDto);

    WithdrawResponseDto withdrawMoneyForCustomer(Long customerId, WithdrawRequestDto withdrawRequestDto);
}
