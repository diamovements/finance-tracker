package org.example.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddGoalRequest(BigDecimal goalAmount, LocalDate endDate) {
}
