package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Limit;
import org.example.entity.RecurringFrequency;
import org.example.exception.LimitNotFoundException;
import org.example.repository.LimitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {
    private final LimitRepository limitRepository;

    public List<Limit> getUsersLimits(UUID userId) {
        return limitRepository.findByUserId(userId);
    }
    public void addLimit(UUID userId, BigDecimal maxExpense, RecurringFrequency frequency) {
        List<Limit> limits = limitRepository.findByUserId(userId);
        Optional<Limit> existingLimit = limits.stream()
                .filter(limit -> limit.getFrequency().equals(frequency))
                .findFirst();

        Limit newLimit;

        if (existingLimit.isPresent()) {
            log.info("Existing limit found: {}", existingLimit.get());
            newLimit = existingLimit.get();
        } else {
            newLimit = new Limit();
            newLimit.setUserId(userId);
        }

        newLimit.setMaxExpenseLimit(maxExpense);
        newLimit.setFrequency(frequency);
        log.info("Saving limit: {}", newLimit);

        limitRepository.save(newLimit);
    }

    public void deleteLimit(UUID userId, RecurringFrequency frequency) {
        List<Limit> limits = limitRepository.findByUserId(userId);

        Optional<Limit> limitToDelete = limits.stream()
                .filter(limit -> limit.getFrequency().equals(frequency))
                .findFirst();

        if (limitToDelete.isPresent()) {
            log.info("Deleting limit: {}", limitToDelete.get());
            limitRepository.delete(limitToDelete.get());
        } else {
            log.warn("No limit found for userId {} with frequency {}", userId, frequency);
            throw new LimitNotFoundException("У вас нет лимита с такой частотой оплаты.");
        }
    }

    public BigDecimal getCurrentLimit(UUID userId) {
        Limit limit = limitRepository.findByUserId(userId).stream().findFirst().orElse(null);
        return limit != null ? limit.getMaxExpenseLimit() : BigDecimal.ZERO;
    }
}
