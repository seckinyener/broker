package com.seckinyener.ing.broker.repository;

import com.seckinyener.ing.broker.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
