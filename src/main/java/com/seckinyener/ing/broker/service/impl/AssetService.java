package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.AssetNotFoundException;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.service.IAssetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AssetService implements IAssetService {

    private final AssetRepository assetRepository;

    @Override
    public Asset findAssetByCustomerIdAndName(Long customerId, String name) {
        return assetRepository.findAssetByCustomerIdAndName(customerId, name).orElseThrow(() -> new AssetNotFoundException("Asset not found with customer id: " + customerId + " and " + name));
    }

    @Override
    public void createAssetForCustomer(BigDecimal amount, Customer customer) {
        Asset customerTRYAsset = new Asset();
        customerTRYAsset.setName("TRY");
        customerTRYAsset.setSize(amount);
        customerTRYAsset.setCustomer(customer);
        customerTRYAsset.setUsableSize(amount);
        assetRepository.save(customerTRYAsset);
    }

    @Override
    public void updateUsableSizeOfAssetBySubtractingAmount(Asset asset, BigDecimal amount) {
        asset.setUsableSize(asset.getUsableSize().subtract(amount));
        assetRepository.save(asset);
    }

    private Asset createAssetWithZeroSize(String assetName, Customer customer) {
        Asset asset = new Asset();
        asset.setName(assetName);
        asset.setCustomer(customer);
        asset.setSize(BigDecimal.ZERO);
        asset.setUsableSize(BigDecimal.ZERO);
        return asset;
    }

    private Asset getAssetOfOrder(Order order) {
        Optional<Asset> optionalCustomerAssetOfOrder = assetRepository.findAssetByCustomerIdAndName(order.getId(), order.getAsset());
        return optionalCustomerAssetOfOrder.orElseGet(() -> createAssetWithZeroSize(order.getAsset(), order.getCustomer()));
    }

    @Override
    public void updateAssetValuesForMatchedOrder(Order order) {
        Asset customerTRYBalance = findAssetByCustomerIdAndName(order.getCustomer().getId(), "TRY");
        Asset customerAssetOfOrder = getAssetOfOrder(order);

        if (SideEnum.SELL.equals(order.getOrderSide())) {
            customerTRYBalance.setSize(customerTRYBalance.getSize().add(order.getPrice().multiply(order.getSize())));
            customerTRYBalance.setUsableSize(customerTRYBalance.getUsableSize().add(order.getPrice().multiply(order.getSize())));
            customerAssetOfOrder.setUsableSize(customerAssetOfOrder.getUsableSize().subtract(order.getSize()));
        } else if (SideEnum.BUY.equals(order.getOrderSide())) {
            customerAssetOfOrder.setSize(customerAssetOfOrder.getSize().add(order.getSize()));
            customerAssetOfOrder.setUsableSize(customerAssetOfOrder.getUsableSize().add(order.getSize()));
        }

        assetRepository.save(customerAssetOfOrder);
        assetRepository.save(customerTRYBalance);
    }

    @Override
    public void updateAssetUsableSizeWhenOrderIsDeleted(Order order) {
        Asset assetTRY = assetRepository.findAssetByCustomerIdAndName(order.getCustomer().getId(), "TRY").orElseThrow(() -> new AssetNotFoundException("Asset not found with customer id: " + order.getCustomer().getId() + " and TRY"));
        BigDecimal orderAmount = order.getPrice().multiply(order.getSize());
        assetTRY.setUsableSize(assetTRY.getUsableSize().add(orderAmount));
        assetRepository.save(assetTRY);
    }
}
