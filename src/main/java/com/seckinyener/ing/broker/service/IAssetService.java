package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.DepositRequestDto;
import com.seckinyener.ing.broker.model.dto.DepositResponseDto;
import com.seckinyener.ing.broker.model.dto.WithdrawRequestDto;
import com.seckinyener.ing.broker.model.dto.WithdrawResponseDto;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;

import java.math.BigDecimal;

public interface IAssetService {

    Asset findAssetByCustomerIdAndName(Long customerId, String name);

    void createAssetForCustomer(BigDecimal amount, Customer customer);

    void updateUsableSizeOfAssetBySubtractingAmount(Asset asset, BigDecimal amount);

    void updateAssetValuesForMatchedOrder(Order order);

    void updateAssetUsableSizeWhenOrderIsDeleted(Order order);

    DepositResponseDto depositMoneyForCustomer(Long customerId, DepositRequestDto depositRequestDto);

    WithdrawResponseDto withdrawMoneyForCustomer(Long customerId, WithdrawRequestDto withdrawRequestDto);
}
