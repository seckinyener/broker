package com.seckinyener.ing.broker.model.dto;

import com.seckinyener.ing.broker.model.enumerated.SideEnum;

import java.math.BigDecimal;

public record CreateOrderDto(Long userId, String asset, SideEnum side, BigDecimal size, BigDecimal price) {
}
