package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.SavingGoal;
import org.example.repository.SavingGoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingGoalService {
    private final SavingGoalRepository savingGoalRepository;

    public List<SavingGoal> getUsersGoals(UUID userId) {
        return savingGoalRepository.findByUserId(userId);
    }

    public void addGoal(UUID userId, BigDecimal goalAmount, LocalDate endDate) {
        List<SavingGoal> goals = savingGoalRepository.findByUserId(userId);

        SavingGoal newGoal;
        if (!goals.isEmpty()) {
            newGoal = goals.get(0);
            log.info("Existing goal found: {}", newGoal);
        } else {
            newGoal = new SavingGoal();
            newGoal.setUserId(userId);
        }

        newGoal.setGoalAmount(goalAmount);
        newGoal.setEndDate(endDate);
        log.info("Saving goal: {}", newGoal);

        savingGoalRepository.save(newGoal);
    }

    public void deleteGoal(UUID userId) {
        List<SavingGoal> goals = savingGoalRepository.findByUserId(userId);

        if (!goals.isEmpty()) {
            SavingGoal goalToDelete = goals.get(0);
            log.info("Deleting goal: {}", goalToDelete);
            savingGoalRepository.delete(goalToDelete);
        } else {
            log.info("No goal found for user: {}", userId);
        }
    }
}
