package com.seckinyener.ing.broker.model.dto;

import java.math.BigDecimal;

public record WithdrawRequestDto(BigDecimal amount, String iban) {
}
