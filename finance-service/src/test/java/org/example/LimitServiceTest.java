package org.example;

import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddLimitRequest;
import org.example.entity.EventType;
import org.example.entity.Limit;
import org.example.entity.RecurringFrequency;
import org.example.exception.LimitNotFoundException;
import org.example.repository.LimitRepository;
import org.example.service.KafkaProducerService;
import org.example.service.LimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LimitServiceTest {
    private final UUID userId = UUID.randomUUID();
    private LimitService limitService;

    @Mock
    private LimitRepository limitRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        limitService = new LimitService(limitRepository, kafkaProducerService);
    }

    @Test
    public void getAllLimitsTest_shouldReturnLimits() {
        Limit limit = mock(Limit.class);

        Mockito.when(limitService.getUsersLimits(userId)).thenReturn(List.of(limit));

        assertEquals(List.of(limit), limitService.getUsersLimits(userId));
    }

    @Test
    public void addLimitTest_shouldAddLimit() {
        AddLimitRequest request  = new AddLimitRequest(BigDecimal.valueOf(1000), RecurringFrequency.MONTHLY);

        limitService.addLimit(userId, request, mock(UserDto.class));

        ArgumentCaptor<Limit> captor = ArgumentCaptor.forClass(Limit.class);
        Mockito.verify(limitRepository, times(1)).save(captor.capture());
        assertEquals(BigDecimal.valueOf(1000), captor.getValue().getMaxExpenseLimit());
        assertEquals(RecurringFrequency.MONTHLY, captor.getValue().getFrequency());
        ArgumentCaptor<NotificationMessage> captor2 = ArgumentCaptor.forClass(NotificationMessage.class);
        Mockito.verify(kafkaProducerService, times(1)).sendMessage(captor2.capture());
        assertEquals(EventType.LIMIT_ADDED, captor2.getValue().eventType());
    }

    @Test
    public void deleteLimitTest_shouldDeleteLimit() {
        Limit limit = mock(Limit.class);

        Mockito.when(limit.getFrequency()).thenReturn(RecurringFrequency.MONTHLY);
        Mockito.when(limitRepository.findByUserId(userId)).thenReturn(List.of(limit));

        limitService.deleteLimit(userId, limit.getFrequency());

        ArgumentCaptor<Limit> captor = ArgumentCaptor.forClass(Limit.class);
        Mockito.verify(limitRepository, times(1)).delete(captor.capture());
        assertEquals(limit.getFrequency(), captor.getValue().getFrequency());
    }

    @Test
    public void deleteLimit_shouldThrowException() {
        Limit limit = mock(Limit.class);

        assertThrows(LimitNotFoundException.class, () -> limitService.deleteLimit(userId, limit.getFrequency()));
        verify(limitRepository, never()).delete(any());
    }

    @Test
    public void getCurrentLimitTest_shouldReturnCurrentLimit() {
        Limit limit = mock(Limit.class);

        Mockito.when(limit.getMaxExpenseLimit()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(limitRepository.findByUserId(userId)).thenReturn(List.of(limit));

        assertEquals(limit.getMaxExpenseLimit(), limitService.getCurrentLimit(userId));
    }
}
