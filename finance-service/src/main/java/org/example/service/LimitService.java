package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddLimitRequest;
import org.example.entity.EventType;
import org.example.entity.Limit;
import org.example.entity.RecurringFrequency;
import org.example.exception.LimitNotFoundException;
import org.example.repository.LimitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {
    private final LimitRepository limitRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public List<Limit> getUsersLimits(UUID userId) {
        return limitRepository.findByUserId(userId);
    }

    public void addLimit(UUID userId, AddLimitRequest request, UserDto data) {
        log.info("Adding limit: {}", request.maxExpenseLimit());
        List<Limit> limits = limitRepository.findByUserId(userId);
        Optional<Limit> existingLimit = limits.stream()
                .filter(limit -> limit.getFrequency().equals(request.frequency()))
                .findFirst();

        Limit newLimit = existingLimit.orElseGet(() -> {
            Limit limit = new Limit();
            limit.setUserId(userId);
            return limit;
        });

        existingLimit.ifPresent(limit -> log.info("Existing limit found: {}", limit.getMaxExpenseLimit()));

        newLimit.setMaxExpenseLimit(request.maxExpenseLimit());
        newLimit.setFrequency(request.frequency());
        log.info("Saving limit: {}", newLimit.getMaxExpenseLimit());

        limitRepository.save(newLimit);

        log.info("Limit saved: {}", limitRepository.findByUserId(userId).get(0).getMaxExpenseLimit());

        kafkaProducerService.sendMessage(new NotificationMessage(userId, data.email(), EventType.LIMIT_ADDED,
                null, request.maxExpenseLimit(), null, null, request.frequency()));

    }
    @Transactional

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
