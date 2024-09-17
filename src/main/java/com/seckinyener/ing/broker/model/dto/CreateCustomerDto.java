package com.seckinyener.ing.broker.model.dto;

import com.seckinyener.ing.broker.model.enumerated.RoleEnum;

public record CreateCustomerDto(String username, String password, RoleEnum role, String tryAmount) {
}
