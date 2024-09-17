package com.seckinyener.ing.broker.model.dto;

import io.micrometer.common.util.StringUtils;

import java.time.LocalDateTime;

public record OrderFilterRequest(Long customerId, String startDate, String endDate ) {
    public LocalDateTime getStartDate() {
        return StringUtils.isNotEmpty(startDate) ? LocalDateTime.parse(startDate) : null;
    }

    public LocalDateTime getEndDate() {
        return StringUtils.isNotEmpty(endDate) ? LocalDateTime.parse(endDate) : null;
    }
}
