package org.example;

import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddGoalRequest;
import org.example.entity.EventType;
import org.example.entity.SavingGoal;
import org.example.exception.GoalNotFoundException;
import org.example.repository.SavingGoalRepository;
import org.example.service.KafkaProducerService;
import org.example.service.SavingGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class SavingGoalServiceTest {

    private final UUID userId = UUID.randomUUID();

    private SavingGoalService savingGoalService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private SavingGoalRepository savingGoalRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        savingGoalService = new SavingGoalService(savingGoalRepository, kafkaProducerService);
    }

    @Test
    public void getAllGoalsTest_shouldReturnGoals() {
        SavingGoal goal = mock(SavingGoal.class);

        Mockito.when(savingGoalService.getUsersGoals(userId)).thenReturn(List.of(goal));

        assertEquals(List.of(goal), savingGoalService.getUsersGoals(userId));
    }

    @Test
    public void addGoalTest_shouldAddGoal() {
        AddGoalRequest request = new AddGoalRequest(BigDecimal.valueOf(1000), LocalDate.now().plusMonths(1));
        UserDto data = mock(UserDto.class);

        savingGoalService.addGoal(userId, request, data);

        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);
        Mockito.verify(savingGoalRepository, times(1)).save(captor.capture());
        assertEquals(request.goalAmount(), captor.getValue().getGoalAmount());
        assertEquals(request.endDate(), captor.getValue().getEndDate());
        ArgumentCaptor<NotificationMessage> captor2 = ArgumentCaptor.forClass(NotificationMessage.class);
        Mockito.verify(kafkaProducerService, times(1)).sendMessage(captor2.capture());
        assertEquals(EventType.GOAL_ADDED, captor2.getValue().eventType());
    }

    @Test
    public void deleteGoalTest_shouldDeleteGoal() {
        SavingGoal goal = mock(SavingGoal.class);

        Mockito.when(goal.getGoalAmount()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(savingGoalRepository.findByUserId(userId)).thenReturn(List.of(goal));

        savingGoalService.deleteGoal(userId);

        ArgumentCaptor<SavingGoal> captor = ArgumentCaptor.forClass(SavingGoal.class);
        Mockito.verify(savingGoalRepository, times(1)).delete(captor.capture());
        assertEquals(goal.getGoalAmount(), captor.getValue().getGoalAmount());
    }

    @Test
    public void deleteGoalTest_shouldThrowException() {
        Mockito.when(savingGoalRepository.findByUserId(userId)).thenReturn(List.of());

        assertThrows(GoalNotFoundException.class, () -> savingGoalService.deleteGoal(userId));
    }
}
