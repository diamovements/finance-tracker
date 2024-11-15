package org.example.dto.response;


import java.math.BigDecimal;

public record AccountDataResponse(String name, String surname, BigDecimal expenses, BigDecimal incomes, BigDecimal balance, BigDecimal limit) {
}
