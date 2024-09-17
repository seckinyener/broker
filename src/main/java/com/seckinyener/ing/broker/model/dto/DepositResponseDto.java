package com.seckinyener.ing.broker.model.dto;

import java.math.BigDecimal;

public record DepositResponseDto(BigDecimal newBalance, BigDecimal newUsableBalance) {
}
