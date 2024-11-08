package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "saving_goals")
@AllArgsConstructor
@NoArgsConstructor
public class SavingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "goal_id")
    private UUID goalId;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "goal_amount")
    @Positive(message = "Значение должно быть больше нуля")
    private BigDecimal goalAmount;
    @Column(name = "end_date")
    @FutureOrPresent(message = "Значение должно быть позже сегодняшней даты")
    private LocalDate endDate;
}
