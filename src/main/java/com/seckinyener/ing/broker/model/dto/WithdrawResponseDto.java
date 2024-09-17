package com.seckinyener.ing.broker.model.dto;

import java.math.BigDecimal;

public record WithdrawResponseDto(BigDecimal newBalance, BigDecimal usableBalance, String iban) {
}
