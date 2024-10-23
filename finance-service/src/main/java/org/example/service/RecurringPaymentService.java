package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.RecurringFrequency;
import org.example.entity.RecurringPayment;
import org.example.repository.RecurringPaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringPaymentService {
    private final RecurringPaymentRepository paymentRepository;

    public void addPayment(UUID userId, String name, BigDecimal amount, LocalDate startDate, RecurringFrequency frequency) {
        List<RecurringPayment> payments = paymentRepository.findByUserId(userId);

        int delta = switch (frequency) {
            case DAILY -> 1;
            case WEEKLY -> 7;
            case MONTHLY -> 30;
        };

        Optional<RecurringPayment> existingPayment = payments.stream()
                .filter(payment -> payment.getFrequency().equals(frequency))
                .findFirst();

        RecurringPayment newPayment;

        if (existingPayment.isPresent()) {
            log.info("Existing payment found: {}", existingPayment.get());
            newPayment = existingPayment.get();
        } else {
            newPayment = new RecurringPayment();
            newPayment.setUserId(userId);
        }

        newPayment.setAmount(amount);
        newPayment.setFrequency(frequency);
        newPayment.setStartDate(startDate);
        newPayment.setNextPaymentDate(startDate.plusDays(delta));
        newPayment.setName(name);

        log.info("Saving payment: {}", newPayment);

        paymentRepository.save(newPayment);
    }

    public List<RecurringPayment> getAllPayments(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    public void deletePayment(UUID userId, String name) {
        Optional<RecurringPayment> deletePayment = paymentRepository.findByUserIdAndName(userId, name);
        log.info("Deleting payment: {}", deletePayment);
        deletePayment.ifPresent(paymentRepository::delete);
    }
}
