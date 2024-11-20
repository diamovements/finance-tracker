package org.example;

import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.example.service.AccountService;
import org.example.service.LimitService;
import org.example.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountServiceTest {
    private final UUID userId = UUID.randomUUID();

    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LimitService limitService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountService = new AccountService(transactionService, accountRepository, limitService);
    }

    @Test
    public void calculateExpensesTest_shouldReturnExpenses() {
        assertDoesNotThrow(() -> accountService.calculateExpenses(userId));
        assertEquals(transactionService.calculateTotalExpense(userId), accountService.calculateExpenses(userId));
    }

    @Test
    public void calculateIncomesTest_shouldReturnIncomes() {
        assertDoesNotThrow(() -> accountService.calculateIncomes(userId));
        assertEquals(transactionService.calculateTotalIncome(userId), accountService.calculateIncomes(userId));
    }

    @Test
    public void getCurrentLimit_shouldReturnLimit() {
        assertDoesNotThrow(() -> accountService.getCurrentLimit(userId));
        assertEquals(limitService.getCurrentLimit(userId), accountService.getCurrentLimit(userId));
    }

    @Test
    public void getCurrentBalance_shouldReturnBalance() {
        BigDecimal storedBalance = BigDecimal.valueOf(10000);
        BigDecimal totalIncomes = BigDecimal.valueOf(5000);
        BigDecimal totalExpenses = BigDecimal.valueOf(3000);

        Mockito.when(accountRepository.findBalanceByUserId(userId)).thenReturn(storedBalance);
        Mockito.when(transactionService.calculateTotalIncome(userId)).thenReturn(totalIncomes);
        Mockito.when(transactionService.calculateTotalExpense(userId)).thenReturn(totalExpenses);

        assertEquals(storedBalance.add(totalIncomes).subtract(totalExpenses), accountService.getCurrentBalance(userId));
    }

    @Test
    public void getCurrentBalance_shouldReturnZero() {
        BigDecimal totalIncomes = BigDecimal.valueOf(5000);
        BigDecimal totalExpenses = BigDecimal.valueOf(3000);

        Mockito.when(accountRepository.findBalanceByUserId(userId)).thenReturn(null);
        Mockito.when(transactionService.calculateTotalIncome(userId)).thenReturn(totalIncomes);
        Mockito.when(transactionService.calculateTotalExpense(userId)).thenReturn(totalExpenses);

        assertEquals(BigDecimal.ZERO, accountService.getCurrentBalance(userId));
    }

    @Test
    public void updateBalance_shouldUpdateBalance() {
        BigDecimal balance = BigDecimal.valueOf(10000);
        Account account = new Account();
        account.setBalance(balance);
        account.setUserId(userId);

        Mockito.when(accountRepository.findAccountByUserId(userId)).thenReturn(Optional.of(account));
        accountService.updateBalance(userId, balance);

        Mockito.verify(accountRepository, Mockito.times(1)).save(account);
        assertEquals(balance, account.getBalance());
    }

}
