package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.AssetDetailsDto;
import com.seckinyener.ing.broker.model.dto.CreateCustomerDto;
import com.seckinyener.ing.broker.model.dto.CustomerDetailsDto;

import java.util.List;

public interface ICustomerService {

    CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto);

    List<AssetDetailsDto> getCustomerAssets(Long customerId);
}
