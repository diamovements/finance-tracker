package org.example.dto.response;

import org.example.entity.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(BigDecimal amount, String category, TransactionType transactionType, String description) {
}
