package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.NotificationMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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

    public void handlePaymentNotification(NotificationMessage message) {
        String content = "Новый регулярный платеж " + message.name() + " с суммой " + message.amount()
                + " рублей. Дата начала: " + message.date();
        mailService.sendMail(message.email(), "Регулярный платеж добавлен!", content, PATH);
    }

    public void handleCategoryNotification(NotificationMessage message) {
        String content = "Новая категория расходов " + message.name();
        mailService.sendMail(message.email(), "Категория расходов добавлена!", content, PATH);
    }

    public void handleLimitNotification(NotificationMessage message) {
        String content = "Новый лимит на сумму " + message.amount() + " рублей. Периодичность лимита: " + message.frequency().toString();
        mailService.sendMail(message.email(), "Лимит добавлен!", content, PATH);
    }

    public void handleTransactionNotification(NotificationMessage message) {
        String content = "Новая транзакция в категории " + message.name() + " на сумму " + message.amount()
                + " рублей. Тип операции: " + message.transactionType().toString();
        mailService.sendMail(message.email(), "Транзакция добавлена!", content, PATH);
    }

    public void handleGoalNotification(NotificationMessage message) {
        String content = "Новая цель на сумму " + message.amount() + " рублей. Дата достижения: " + message.date();
        mailService.sendMail(message.email(), "Цель добавлена!", content, PATH);
    }



}
