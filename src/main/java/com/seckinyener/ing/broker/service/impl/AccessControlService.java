package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.exception.OrderNotFoundException;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.repository.OrderRepository;
import com.seckinyener.ing.broker.service.IAccessControlService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AccessControlService implements IAccessControlService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    @Override
    public boolean isCustomerAuthorizedByCustomerName(Long customerId, String customerName) {
        Customer customer = customerRepository.findCustomerByUsername(customerName).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return customer.getId().equals(customerId);
    }

    @Override
    public boolean isCustomerAuthorizedByOrder(Long orderId, String customerName) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));
        Customer customer = order.getCustomer();
        return customer.getUsername().equalsIgnoreCase(customerName);
    }
}
