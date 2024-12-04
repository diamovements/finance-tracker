package org.example;

import org.example.dto.EventType;
import org.example.dto.NotificationMessage;
import org.example.dto.PasswordResetNotification;
import org.example.dto.RecurringFrequency;
import org.example.dto.TransactionType;
import org.example.service.MailService;
import org.example.service.NotificationConsumerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private MailService mailService;

    private UUID userId = UUID.randomUUID();

    @InjectMocks
    private NotificationConsumerService notificationConsumerService;

    public NotificationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handlePaymentNotification_ShouldCallSendMail() {
        NotificationMessage message = new NotificationMessage(userId, "email@mail.com", EventType.PAYMENT_ADDED,
                "test", BigDecimal.valueOf(1000), LocalDate.of(2024, 11, 12), null, RecurringFrequency.MONTHLY);
        notificationConsumerService.handlePaymentNotification(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templatePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mailService, times(1)).sendMail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                eq("static/img/banner.png"),
                templatePathCaptor.capture(),
                modelCaptor.capture()
        );

        assertEquals("email@mail.com", emailCaptor.getValue());
        assertEquals("Регулярный платеж добавлен!", subjectCaptor.getValue());
        assertEquals("payment-added.vm", templatePathCaptor.getValue());
        assertEquals("test", modelCaptor.getValue().get("name"));
        assertEquals(BigDecimal.valueOf(1000), modelCaptor.getValue().get("amount"));
    }

    @Test
    void listenNotifications_ShouldCallHandlePaymentNotification() {
        NotificationMessage message = new NotificationMessage(userId, "email@mail.com", EventType.PAYMENT_ADDED,
                "test", BigDecimal.valueOf(1000), LocalDate.of(2024, 11, 12), null, RecurringFrequency.MONTHLY);

        NotificationConsumerService spyService = spy(notificationConsumerService);

        spyService.listenNotifications(message);

        verify(spyService, times(1)).handlePaymentNotification(message);
    }

    @Test
    void listenNotifications_ShouldCallHandleCategoryNotification() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com",
                EventType.CATEGORY_ADDED, "test", null, null, null, null);

        NotificationConsumerService spyService = spy(notificationConsumerService);

        spyService.listenNotifications(message);

        verify(spyService, times(1)).handleCategoryNotification(message);
    }

    @Test
    void listenNotifications_ShouldCallHandleLimitNotification() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com",
                EventType.LIMIT_ADDED, "test", null, null, null, null);
        NotificationConsumerService spyService = spy(notificationConsumerService);

        spyService.listenNotifications(message);

        verify(spyService, times(1)).handleLimitNotification(message);
    }

    @Test
    void listenNotifications_ShouldCallHandleGoalNotification() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com", EventType.GOAL_ADDED,
                null, BigDecimal.valueOf(1000), LocalDate.of(2024, 12, 31), null, null);

        NotificationConsumerService spyService = spy(notificationConsumerService);

        spyService.listenNotifications(message);

        verify(spyService, times(1)).handleGoalNotification(message);
    }

    @Test
    void listenNotifications_ShouldCallHandleTransactionNotification() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com", EventType.TRANSACTION_ADDED,
                "Test", BigDecimal.valueOf(1000), null, TransactionType.EXPENSE, null);

        NotificationConsumerService spyService = spy(notificationConsumerService);

        spyService.listenNotifications(message);

        verify(spyService, times(1)).handleTransactionNotification(message);
    }

    @Test
    void handleCategoryNotification_ShouldCallSendMail() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com",
                EventType.CATEGORY_ADDED, "test", null, null, null, null);

        notificationConsumerService.handleCategoryNotification(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templatePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mailService, times(1)).sendMail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                eq("static/img/banner.png"),
                templatePathCaptor.capture(),
                modelCaptor.capture()
        );

        assertEquals("email@example.com", emailCaptor.getValue());
        assertEquals("Категория добавлена!", subjectCaptor.getValue());
        assertEquals("category-added.vm", templatePathCaptor.getValue());
        assertEquals("test", modelCaptor.getValue().get("name"));
    }

    @Test
    void handleLimitNotification_ShouldCallSendMail() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com",
                EventType.LIMIT_ADDED, "test", null, null, null, null);

        notificationConsumerService.handleLimitNotification(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templatePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mailService, times(1)).sendMail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                eq("static/img/banner.png"),
                templatePathCaptor.capture(),
                modelCaptor.capture()
        );

        assertEquals("email@example.com", emailCaptor.getValue());
        assertEquals("Лимит добавлен!", subjectCaptor.getValue());
        assertEquals("limit-added.vm", templatePathCaptor.getValue());
    }

    @Test
    void handleGoalNotification_ShouldCallSendMail() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com", EventType.GOAL_ADDED,
                null, BigDecimal.valueOf(1000), LocalDate.of(2024, 12, 31), null, null);

        notificationConsumerService.handleGoalNotification(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templatePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mailService, times(1)).sendMail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                eq("static/img/banner.png"),
                templatePathCaptor.capture(),
                modelCaptor.capture()
        );

        assertEquals("email@example.com", emailCaptor.getValue());
        assertEquals("Цель добавлена!", subjectCaptor.getValue());
        assertEquals("goal-added.vm", templatePathCaptor.getValue());
        assertEquals(BigDecimal.valueOf(1000), modelCaptor.getValue().get("amount"));
        assertEquals(LocalDate.of(2024, 12, 31), modelCaptor.getValue().get("date"));
    }

    @Test
    void handleTransactionNotification_ShouldCallSendMail() {
        NotificationMessage message = new NotificationMessage(userId, "email@example.com", EventType.TRANSACTION_ADDED,
               "Test", BigDecimal.valueOf(1000), null, TransactionType.EXPENSE, null);

        notificationConsumerService.handleTransactionNotification(message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> templatePathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> modelCaptor = ArgumentCaptor.forClass(Map.class);

        verify(mailService, times(1)).sendMail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                eq("static/img/banner.png"),
                templatePathCaptor.capture(),
                modelCaptor.capture()
        );

        assertEquals("email@example.com", emailCaptor.getValue());
        assertEquals("Транзакция добавлена!", subjectCaptor.getValue());
        assertEquals("transaction-added.vm", templatePathCaptor.getValue());
        assertEquals("Test", modelCaptor.getValue().get("name"));
        assertEquals(BigDecimal.valueOf(1000), modelCaptor.getValue().get("amount"));
        assertEquals(TransactionType.EXPENSE, modelCaptor.getValue().get("transactionType"));
    }

    @Test
    void listenResetNotifications_ShouldCallHandleResetPasswordNotification() {
        PasswordResetNotification message = new PasswordResetNotification(
                "email@example.com",
                "reset-code"
        );

        NotificationConsumerService spyService = spy(notificationConsumerService);

        spyService.listenResetNotifications(message);

        verify(spyService, times(1)).handleResetPasswordNotification(message);
    }
}
