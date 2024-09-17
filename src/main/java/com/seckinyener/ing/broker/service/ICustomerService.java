package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.CreateCustomerDto;
import com.seckinyener.ing.broker.model.dto.CustomerDetailsDto;

public interface ICustomerService {

    CustomerDetailsDto createCustomer(CreateCustomerDto createCustomerDto);
}
