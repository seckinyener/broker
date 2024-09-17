package com.seckinyener.ing.broker.model.dto;

import com.seckinyener.ing.broker.model.enumerated.RoleEnum;

public record CustomerDetailsDto(String username, RoleEnum role) {
}
