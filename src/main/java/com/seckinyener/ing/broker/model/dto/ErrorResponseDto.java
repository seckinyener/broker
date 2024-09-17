package com.seckinyener.ing.broker.model.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(String message, int status, LocalDateTime timestamp) {
}
