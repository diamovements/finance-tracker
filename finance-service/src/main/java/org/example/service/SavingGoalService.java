package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddGoalRequest;
import org.example.entity.EventType;
import org.example.entity.SavingGoal;
import org.example.exception.GoalNotFoundException;
import org.example.repository.SavingGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingGoalService {
    private final SavingGoalRepository savingGoalRepository;
    private final KafkaProducerService kafkaProducerService;

    public List<SavingGoal> getUsersGoals(UUID userId) {
        return savingGoalRepository.findByUserId(userId);
    }

    public void addGoal(UUID userId, AddGoalRequest request, UserDto data) {
        List<SavingGoal> goals = savingGoalRepository.findByUserId(userId);

        SavingGoal newGoal;
        if (!goals.isEmpty()) {
            newGoal = goals.get(0);
            log.info("Existing goal found: {}", newGoal);
        } else {
            newGoal = new SavingGoal();
            newGoal.setUserId(userId);
        }

        newGoal.setGoalAmount(request.goalAmount());
        newGoal.setEndDate(request.endDate());
        log.info("Saving goal: {}", newGoal);

        savingGoalRepository.save(newGoal);

        kafkaProducerService.sendMessage(new NotificationMessage(userId, data.email(), EventType.GOAL_ADDED,
                null, request.goalAmount(), request.endDate(), null, null));
    }

    public void deleteGoal(UUID userId) {
        List<SavingGoal> goals = savingGoalRepository.findByUserId(userId);

        if (!goals.isEmpty()) {
            SavingGoal goalToDelete = goals.get(0);
            log.info("Deleting goal: {}", goalToDelete);
            savingGoalRepository.delete(goalToDelete);
        } else {
            log.error("No goal found for user: {}", userId);
            throw new GoalNotFoundException("У вас нет целей для удаления.");
        }
    }
}
