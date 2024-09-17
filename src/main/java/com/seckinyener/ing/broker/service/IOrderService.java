package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;
import com.seckinyener.ing.broker.model.dto.OrderFilterRequest;

import java.util.List;

public interface IOrderService {

    OrderDetailsDto createOrder(CreateOrderDto createOrderDto);

    List<OrderDetailsDto> getOrderListOfUserForDateRange(OrderFilterRequest orderFilterRequest);

    OrderDetailsDto deleteOrder(Long orderId);
}
