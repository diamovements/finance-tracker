package org.example;

import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddPaymentRequest;
import org.example.entity.EventType;
import org.example.entity.RecurringFrequency;
import org.example.entity.RecurringPayment;
import org.example.exception.PaymentNotFoundException;
import org.example.repository.RecurringPaymentRepository;
import org.example.service.KafkaProducerService;
import org.example.service.RecurringPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RecurringPaymentServiceTest {

    private final UUID userId = UUID.randomUUID();

    private RecurringPaymentService paymentService;

    @Mock
    private RecurringPaymentRepository paymentRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new RecurringPaymentService(paymentRepository, kafkaProducerService);
    }

    @Test
    public void getAllPaymentsTest_shouldReturnPayments() {
        RecurringPayment payment = mock(RecurringPayment.class);

        Mockito.when(paymentService.getAllPayments(userId)).thenReturn(List.of(payment));

        assertEquals(List.of(payment), paymentService.getAllPayments(userId));
    }

    @Test
    public void addPaymentTest_shouldAddPayment() {
        AddPaymentRequest request = new AddPaymentRequest("Test", BigDecimal.valueOf(1000),
                LocalDate.of(2024, 11, 11), RecurringFrequency.MONTHLY);
        UserDto data = mock(UserDto.class);

        paymentService.addPayment(userId, request, data);

        ArgumentCaptor<RecurringPayment> captor = ArgumentCaptor.forClass(RecurringPayment.class);
        Mockito.verify(paymentRepository, times(1)).save(captor.capture());
        assertEquals(request.name(), captor.getValue().getName());
        assertEquals(request.amount(), captor.getValue().getAmount());
        ArgumentCaptor<NotificationMessage> captor2 = ArgumentCaptor.forClass(NotificationMessage.class);
        Mockito.verify(kafkaProducerService, times(1)).sendMessage(captor2.capture());
        assertEquals(EventType.PAYMENT_ADDED, captor2.getValue().eventType());
    }

    @Test
    public void deletePaymentTest_shouldDeletePayment() {
        RecurringPayment payment = mock(RecurringPayment.class);

        Mockito.when(payment.getName()).thenReturn("Test");
        Mockito.when(paymentRepository.findByUserIdAndName(userId, payment.getName())).thenReturn(Optional.of(payment));
        paymentService.deletePayment(userId, payment.getName());

        ArgumentCaptor<RecurringPayment> captor = ArgumentCaptor.forClass(RecurringPayment.class);
        Mockito.verify(paymentRepository, times(1)).delete(captor.capture());
        assertEquals(payment.getName(), captor.getValue().getName());
    }

    @Test
    public void deletePaymentTest_shouldThrowException() {
        RecurringPayment payment = mock(RecurringPayment.class);

        Mockito.when(payment.getName()).thenReturn("Test");
        Mockito.when(paymentRepository.findByUserIdAndName(userId, payment.getName())).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.deletePayment(userId, payment.getName()));
        verify(paymentRepository, never()).delete(any());
    }

}
