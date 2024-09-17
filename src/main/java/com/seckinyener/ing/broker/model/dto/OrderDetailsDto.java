package com.seckinyener.ing.broker.model.dto;

import com.seckinyener.ing.broker.model.enumerated.SideEnum;
import com.seckinyener.ing.broker.model.enumerated.StatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDetailsDto(String asset, BigDecimal size, BigDecimal price, StatusEnum status, SideEnum side, LocalDateTime createDate) {
}
