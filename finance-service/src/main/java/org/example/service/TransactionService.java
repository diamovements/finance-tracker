package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Category;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public void addTransaction(UUID userId, BigDecimal amount, TransactionType type, String categoryName, String description) {
        Optional<Category> addTransactionTo = categoryService.getCategoryByName(categoryName, userId);
        Transaction transaction = new Transaction();
        transaction.setCategory(addTransactionTo.get());
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setUserId(userId);
        transactionRepository.save(transaction);

    }

    public List<Transaction> getAllTransactions(UUID userId) {
        return transactionRepository.findByUserId(userId);
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
}
