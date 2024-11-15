package org.example.dto;

import org.example.dto.EventType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record NotificationMessage(UUID userId, String email, EventType eventType, String name,
                                  BigDecimal amount, LocalDate date, TransactionType transactionType,
                                  RecurringFrequency frequency) {
}
