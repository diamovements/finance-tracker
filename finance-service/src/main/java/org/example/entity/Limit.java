package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "limits")
@AllArgsConstructor
@NoArgsConstructor
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "limit_id")
    private UUID limitId;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "max_expense_limit")
    private BigDecimal maxExpenseLimit;
    @Enumerated(EnumType.STRING)
    @Column(name = "limit_frequency")
    private RecurringFrequency frequency;
}
