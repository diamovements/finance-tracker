package org.example.dto.request;

import org.example.entity.TransactionType;

import java.math.BigDecimal;

public record AddTransactionRequest(BigDecimal amount, TransactionType transactionType, String category, String description) {
}
