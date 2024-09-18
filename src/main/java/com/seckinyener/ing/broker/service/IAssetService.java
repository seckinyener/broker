package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;

import java.math.BigDecimal;

public interface IAssetService {

    Asset findAssetByCustomerIdAndName(Long customerId, String name);

    void createAssetForCustomer(BigDecimal amount, Customer customer);

    void updateUsableSizeOfAssetBySubtractingAmount(Asset asset, BigDecimal amount);
}
