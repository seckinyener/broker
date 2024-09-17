package com.seckinyener.ing.broker.repository;

import com.seckinyener.ing.broker.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByUsername(String username);

    Optional<Customer> findCustomerById(Long id);
}
