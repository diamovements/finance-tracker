package org.example.dto.request;

import org.example.entity.RecurringFrequency;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddPaymentRequest(String name, BigDecimal amount, LocalDate startDate, RecurringFrequency frequency) {
}
