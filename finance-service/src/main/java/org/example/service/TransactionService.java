package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddTransactionRequest;
import org.example.entity.Category;
import org.example.entity.EventType;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.exception.CategoryNotFoundException;
import org.example.exception.LimitExceedException;
import org.example.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final LimitService limitService;
    private final KafkaProducerService kafkaProducerService;

    public void addTransaction(UUID userId, AddTransactionRequest request, UserDto data) {
        log.debug("Id of user to add transaction: {}", userId);
        Optional<Category> addTransactionTo = categoryService.getCategoryByName(request.category(), userId);
        log.info("Category: {}", addTransactionTo);
        if (addTransactionTo.isEmpty()) {
            throw new CategoryNotFoundException("Категория " + request.category() + " не найдена.");
        }
        Transaction transaction = new Transaction();
        validateExpenseLimit(userId, request);
        transaction.setCategory(addTransactionTo.orElse(null));
        transaction.setAmount(request.amount());
        transaction.setTransactionType(request.transactionType());
        transaction.setDescription(request.description());
        transaction.setUserId(userId);

        transactionRepository.save(transaction);

        kafkaProducerService.sendMessage(new NotificationMessage(userId, data.email(), EventType.TRANSACTION_ADDED,
                request.category(), request.amount(), null, request.transactionType(), null));

    }

    public Page<Transaction> getAllTransactions(UUID userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return transactionRepository.findByUserId(userId, pageRequest);
    }

    public BigDecimal calculateTotalExpense(UUID userId) {
        return transactionRepository.findByUserId(userId).stream()
                .filter(transaction -> transaction.getTransactionType().equals(TransactionType.EXPENSE))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalIncome(UUID userId) {
        return transactionRepository.findByUserId(userId).stream()
                .filter(transaction -> transaction.getTransactionType().equals(TransactionType.INCOME))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validateExpenseLimit(UUID userId, AddTransactionRequest request) {
        if (request.transactionType() == TransactionType.EXPENSE) {
            BigDecimal currentLimit = limitService.getCurrentLimit(userId);
            if (Objects.equals(currentLimit, BigDecimal.ZERO)) {
                return;
            }
            BigDecimal totalExpense = calculateTotalExpense(userId);
            BigDecimal newExpense = totalExpense.add(request.amount());
            if (newExpense.compareTo(currentLimit) > 0) {
                throw new LimitExceedException("Вы превысили лимит трат");
            }
        }
    }
}
