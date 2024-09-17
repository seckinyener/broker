package com.seckinyener.ing.broker.repository;

import com.seckinyener.ing.broker.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
