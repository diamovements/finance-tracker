package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.NotificationMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public void sendMessage(NotificationMessage message) {
        kafkaTemplate.send("notification_topic", message);
    }
}
