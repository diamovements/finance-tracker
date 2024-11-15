package org.example.dto.request;

import org.example.entity.RecurringFrequency;

import java.math.BigDecimal;

public record AddLimitRequest(BigDecimal maxExpenseLimit, RecurringFrequency frequency) {
}
