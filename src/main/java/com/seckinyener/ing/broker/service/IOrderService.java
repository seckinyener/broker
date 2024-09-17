package com.seckinyener.ing.broker.service;

import com.seckinyener.ing.broker.model.dto.CreateOrderDto;
import com.seckinyener.ing.broker.model.dto.OrderDetailsDto;

public interface IOrderService {

    OrderDetailsDto createOrder(CreateOrderDto createOrderDto);
}
