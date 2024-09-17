package com.seckinyener.ing.broker.service.impl;

import com.seckinyener.ing.broker.exception.CustomerNotFoundException;
import com.seckinyener.ing.broker.model.dto.AssetDetailsDto;
import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;
import com.seckinyener.ing.broker.model.dto.OrderFilterRequest;
import com.seckinyener.ing.broker.model.entity.Customer;
import com.seckinyener.ing.broker.model.entity.Order;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;
import com.seckinyener.ing.broker.repository.CustomerRepository;
import com.seckinyener.ing.broker.repository.OrderRepository;
import com.seckinyener.ing.broker.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @Override
    public List<OrderDetailsDto> getOrderListOfUserForDateRange(OrderFilterRequest orderFilterRequest) {
        Customer customer = customerRepository.findCustomerById(orderFilterRequest.customerId())
                .orElseThrow((() -> new CustomerNotFoundException("Customer not found with id: " + orderFilterRequest.customerId())));
        List<Order> orderList;
        if (Objects.isNull(orderFilterRequest.getStartDate()) && Objects.isNull(orderFilterRequest.getEndDate())){
           orderList = customer.getOrders();
        } else if (Objects.isNull(orderFilterRequest.getStartDate())){
            orderList = orderRepository.findAllByCustomerIdAndCreateDateBefore(orderFilterRequest.customerId(), orderFilterRequest.getEndDate());
        } else if ( Objects.isNull(orderFilterRequest.getEndDate())){
            orderList = orderRepository.findAllByCustomerIdAndCreateDateAfter(orderFilterRequest.customerId(), orderFilterRequest.getStartDate());
        } else {
            orderList = orderRepository.findAllByCustomerIdAndCreateDateBetween(orderFilterRequest.customerId(), orderFilterRequest.getStartDate(), orderFilterRequest.getEndDate());
        }

        return orderList.stream().sorted(Comparator.comparing(Order::getCreateDate).reversed()).map(item -> new OrderDetailsDto(item.getAsset(), item.getSize(), item.getPrice()
                , item.getStatus(), item.getOrderSide(), item.getCreateDate())).toList();
    }
}
