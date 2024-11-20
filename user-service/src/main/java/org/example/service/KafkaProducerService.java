package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PasswordResetNotification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, PasswordResetNotification> kafkaTemplate;

    public void sendMessage(PasswordResetNotification message) {
        kafkaTemplate.send("reset_topic", message);
    }
}
