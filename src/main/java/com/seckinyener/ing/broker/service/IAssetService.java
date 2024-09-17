package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.entity.Asset;

public interface IAssetService {

    Asset findAssetByCustomerIdAndName(Long customerId, String name);
}
