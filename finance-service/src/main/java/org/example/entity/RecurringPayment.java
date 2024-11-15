package org.example.entity;

import jakarta.persistence.*;
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
@Table(name = "recurring_payments")
@AllArgsConstructor
@NoArgsConstructor
public class RecurringPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;
    @Column(name = "payment_name")
    private String name;
    @Column(name = "payment_amount")
    @Positive(message = "Значение должно быть больше нуля")
    private BigDecimal amount;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency")
    private RecurringFrequency frequency;
    @Column(name = "user_id")
    private UUID userId;

}
