package com.seckinyener.ing.broker.repository;

import com.seckinyener.ing.broker.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);

    List<Order> findAllByCustomerIdAndCreateDateAfter(Long customerId, LocalDateTime start);

    List<Order> findAllByCustomerIdAndCreateDateBefore(Long customerId, LocalDateTime end);

    Optional<Order> findOrderById(Long id);
}
