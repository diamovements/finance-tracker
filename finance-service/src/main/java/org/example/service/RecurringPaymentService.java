package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddPaymentRequest;
import org.example.entity.EventType;
import org.example.entity.RecurringPayment;
import org.example.exception.PaymentNotFoundException;
import org.example.repository.RecurringPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringPaymentService {
    private final RecurringPaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;

    public void addPayment(UUID userId, AddPaymentRequest request, UserDto data) {
        List<RecurringPayment> payments = paymentRepository.findByUserId(userId);

        int delta = switch (request.frequency()) {
            case DAILY -> 1;
            case WEEKLY -> 7;
            case MONTHLY -> 30;
        };

        Optional<RecurringPayment> existingPayment = payments.stream()
                .filter(payment -> payment.getFrequency().equals(request.frequency()))
                .findFirst();

        RecurringPayment newPayment = existingPayment.orElseGet(() -> {
            RecurringPayment payment = new RecurringPayment();
            payment.setUserId(userId);
            return payment;
        });
        existingPayment.ifPresent(payment -> log.info("Existing payment found: {}", payment));

        newPayment.setAmount(request.amount());
        newPayment.setFrequency(request.frequency());
        newPayment.setStartDate(request.startDate());
        newPayment.setNextPaymentDate(request.startDate().plusDays(delta));
        newPayment.setName(request.name());

        log.info("Saving payment: {}", newPayment);

        paymentRepository.save(newPayment);

        kafkaProducerService.sendMessage(new NotificationMessage(userId, data.email(), EventType.PAYMENT_ADDED,
                request.name(), request.amount(), request.startDate(), null, request.frequency()));
    }

    public List<RecurringPayment> getAllPayments(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    public void deletePayment(UUID userId, String name) {
        Optional<RecurringPayment> deletePayment = paymentRepository.findByUserIdAndName(userId, name);
        log.info("Deleting payment: {}", deletePayment);
        if (deletePayment.isEmpty()) {
            log.error("Payment not found: {}", name);
            throw new PaymentNotFoundException("Payment not found");
        }
        else {
            paymentRepository.delete(deletePayment.get());
        }
    }
}
