package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.repository.OrderRepository;
import com.seckinyener.ing.broker.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class OrderService implements IOrderService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderDetailsDto createOrder(CreateOrderDto createOrderDto) {
        Customer customer = customerRepository.findCustomerById(createOrderDto.userId())
                .orElseThrow((() -> new CustomerNotFoundException("Customer not found with id: " + createOrderDto.userId())));
        Order order = new Order();
        order.setOrderSide(createOrderDto.side());
        order.setCustomer(customer);
        order.setStatus(StatusEnum.PENDING);
        order.setSize(createOrderDto.size());
        order.setPrice(createOrderDto.price());
        order.setAsset(createOrderDto.asset());
        orderRepository.save(order);
        return new OrderDetailsDto(order.getAsset(), order.getSize(), order.getPrice(), order.getStatus(), order.getOrderSide(), order.getCreateDate());
    }
}
