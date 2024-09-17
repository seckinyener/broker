package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.AssetNotFoundException;
import com.seckinyener.ing.broker.model.entity.Asset;
import com.seckinyener.ing.broker.repository.AssetRepository;
import com.seckinyener.ing.broker.service.IAssetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AssetService implements IAssetService {

    private final AssetRepository assetRepository;

    @Override
    public Asset findAssetByCustomerIdAndName(Long customerId, String name) {
        return assetRepository.findAssetByCustomerIdAndName(customerId, name).orElseThrow(() -> new AssetNotFoundException("Asset not found with customer id: " + customerId + " and " + name));
    }
}
