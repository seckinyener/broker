package com.seckinyener.ing.broker.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AssetDetailsDto(String name, BigDecimal size, BigDecimal usableSize, LocalDateTime updateDate) {
}
