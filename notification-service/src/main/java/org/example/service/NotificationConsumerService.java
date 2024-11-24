package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.NotificationMessage;
import org.example.dto.PasswordResetNotification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationConsumerService {
    private final MailService mailService;
    private final static String PATH = "static/img/banner.png";

    @KafkaListener(topics = "notification_topic", groupId = "my-group")
    public void listenNotifications(NotificationMessage message) {
        switch (message.eventType()) {
            case CATEGORY_ADDED -> handleCategoryNotification(message);
            case GOAL_ADDED -> handleGoalNotification(message);
            case LIMIT_ADDED -> handleLimitNotification(message);
            case PAYMENT_ADDED -> handlePaymentNotification(message);
            case TRANSACTION_ADDED -> handleTransactionNotification(message);
            default -> System.out.println("Получено неизвестное уведомление");
        }
    }

    @KafkaListener(topics = "reset_topic", groupId = "my-group")
    public void listenResetNotifications(PasswordResetNotification message) {
        handleResetPasswordNotification(message);
    }

    public void handlePaymentNotification(NotificationMessage message) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", message.name());
        model.put("amount", message.amount());
        model.put("date", message.date());

        mailService.sendMail(message.email(), "Регулярный платеж добавлен!", PATH, "payment-added.vm", model);
    }

    public void handleResetPasswordNotification(PasswordResetNotification message) {
        Map<String, Object> model = new HashMap<>();
        model.put("code", message.code());
        model.put("email", message.email());

        mailService.sendMail(message.email(), "Подтверждение сброса пароля", PATH, "reset-password.vm", model);
    }

    public void handleCategoryNotification(NotificationMessage message) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", message.name());

        mailService.sendMail(message.email(), "Категория добавлена!", PATH, "category-added.vm", model);
    }

    public void handleLimitNotification(NotificationMessage message) {
        Map<String, Object> model = new HashMap<>();
        model.put("amount", message.amount());
        model.put("frequency", message.frequency());

        mailService.sendMail(message.email(), "Лимит добавлен!", PATH, "limit-added.vm", model);
    }

    public void handleTransactionNotification(NotificationMessage message) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", message.name());
        model.put("amount", message.amount());
        model.put("transactionType", message.transactionType());

        mailService.sendMail(message.email(), "Транзакция добавлена!", PATH, "transaction-added.vm", model);
    }

    public void handleGoalNotification(NotificationMessage message) {
        Map<String, Object> model = new HashMap<>();
        model.put("amount", message.amount());
        model.put("date", message.date());

        mailService.sendMail(message.email(), "Цель добавлена!", PATH, "goal-added.vm", model);
    }

}
