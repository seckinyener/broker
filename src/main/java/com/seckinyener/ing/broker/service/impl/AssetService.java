package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.AssetNotFoundException;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.service.IAssetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
}
