package com.seckinyener.ing.broker.model.dto;

import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;

import java.time.LocalDateTime;

public record OrderDetailsDto(String asset, double size, double price, StatusEnum status, SideEnum side, LocalDateTime createDate) {
}
