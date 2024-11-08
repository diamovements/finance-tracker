package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final LimitService limitService;

    public BigDecimal calculateExpenses(UUID userId) {
        return transactionService.calculateTotalExpense(userId);
    }

    public BigDecimal calculateIncomes(UUID userId) {
        return transactionService.calculateTotalIncome(userId);
    }

    @Transactional
    public BigDecimal getCurrentBalance(UUID userId) {
        BigDecimal storedBalance = accountRepository.findBalanceByUserId(userId);
        log.info("Current balance: {}", storedBalance);
        return storedBalance == null ? BigDecimal.ZERO :
                storedBalance.add(calculateIncomes(userId)).subtract(calculateExpenses(userId));
    }

    @Transactional
    public void updateBalance(UUID userId, BigDecimal balance) {
        Account account = accountRepository.findAccountByUserId(userId).orElseGet(() -> {
            Account newAccount = new Account();
            newAccount.setUserId(userId);
            newAccount.setBalance(balance);
            return newAccount;
        });
        account.setBalance(balance);
        accountRepository.save(account);
        log.info("Balance updated: {}", balance);
    }

    public BigDecimal getCurrentLimit(UUID userId) {
        return limitService.getCurrentLimit(userId);
    }
}
