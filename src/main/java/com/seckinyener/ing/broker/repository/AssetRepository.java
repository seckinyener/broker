package com.seckinyener.ing.broker.repository;

import com.seckinyener.ing.broker.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findAssetByCustomerIdAndName(Long customerId, String name);
}
