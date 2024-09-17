package com.seckinyener.ing.broker.model.dto;

import com.seckinyener.ing.broker.model.enumerated.SideEnum;

public record CreateOrderDto(Long userId, String asset, SideEnum side, double size, double price) {
}
