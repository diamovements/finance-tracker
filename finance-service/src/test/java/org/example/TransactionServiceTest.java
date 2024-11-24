package org.example;

import org.example.dto.NotificationMessage;
import org.example.dto.UserDto;
import org.example.dto.request.AddTransactionRequest;
import org.example.entity.Category;
import org.example.entity.EventType;
import org.example.entity.Limit;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.exception.CategoryNotFoundException;
import org.example.exception.LimitExceedException;
import org.example.repository.TransactionRepository;
import org.example.service.CategoryService;
import org.example.service.KafkaProducerService;
import org.example.service.LimitService;
import org.example.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class TransactionServiceTest {

    private final UUID userId = UUID.randomUUID();

    private TransactionService transactionService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LimitService limitService;

    @Spy
    @InjectMocks
    private TransactionService transactionServiceSpy;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(transactionRepository, categoryService, limitService, kafkaProducerService);
    }

    @Test
    public void getAllTransactionsTest_shouldReturnTransactions() {
        Transaction transaction = mock(Transaction.class);
        PageRequest pageRequest = PageRequest.of(1, 5);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));

        Mockito.when(transactionRepository.findByUserId(userId, pageRequest)).thenReturn(transactionPage);
        Page<Transaction> result = transactionService.getAllTransactions(userId, 1, 5);

        assertEquals(transactionPage, result);
        Mockito.verify(transactionRepository, times(1)).findByUserId(userId, pageRequest);
    }

    @Test
    public void calculateTotalExpenseTest_shouldReturnTotalExpense() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTransactionType(TransactionType.EXPENSE);

        Mockito.when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));
        BigDecimal result = transactionService.calculateTotalExpense(userId);

        assertEquals(transaction.getAmount(), result);
        Mockito.verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void calculateTotalIncomeTest_shouldReturnTotalIncome() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTransactionType(TransactionType.INCOME);

        Mockito.when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));
        BigDecimal result = transactionService.calculateTotalIncome(userId);

        assertEquals(transaction.getAmount(), result);
        Mockito.verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void validateExpenseLimitTest_shouldValidateLimit() {
        AddTransactionRequest expenseRequest = new AddTransactionRequest(BigDecimal.valueOf(1000), TransactionType.EXPENSE, "test", "test");
        Transaction transaction = new Transaction();
        transaction.setTransactionType(expenseRequest.transactionType());
        transaction.setAmount(expenseRequest.amount());
        transaction.setDescription(expenseRequest.description());
        transaction.setCategory(new Category());

        Mockito.when(limitService.getCurrentLimit(userId)).thenReturn(new BigDecimal(2000));
        Mockito.when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));
        Mockito.doReturn(new BigDecimal(1000)).when(transactionServiceSpy).calculateTotalExpense(userId);

        assertDoesNotThrow(() -> transactionService.validateExpenseLimit(userId, expenseRequest));

    }

    @Test
    public void validateExpenseLimitTest_shouldThrowException() {
        AddTransactionRequest expenseRequest = new AddTransactionRequest(BigDecimal.valueOf(2000), TransactionType.EXPENSE, "test", "test");
        Transaction transaction = new Transaction();
        transaction.setTransactionType(expenseRequest.transactionType());
        transaction.setAmount(expenseRequest.amount());
        transaction.setDescription(expenseRequest.description());
        transaction.setCategory(new Category());

        Mockito.when(limitService.getCurrentLimit(userId)).thenReturn(new BigDecimal(1000));
        Mockito.when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));
        Mockito.doReturn(new BigDecimal(2000)).when(transactionServiceSpy).calculateTotalExpense(userId);

        assertThrows(LimitExceedException.class, () -> transactionService.validateExpenseLimit(userId, expenseRequest));

    }

    @Test
    public void addTransactionTest_shouldAddTransaction() {
        UserDto data = new UserDto("test@example.com", "John", "Doe");
        AddTransactionRequest request = new AddTransactionRequest(BigDecimal.valueOf(1000), TransactionType.EXPENSE,
                "TestCategory", "Test transaction");

        Category category = new Category();
        category.setName("TestCategory");
        category.setUserId(userId);

        Limit limit = new Limit();
        limit.setMaxExpenseLimit(BigDecimal.valueOf(2000));

        Mockito.when(categoryService.getCategoryByName(eq("TestCategory"), eq(userId))).thenReturn(Optional.of(category));
        Mockito.doNothing().when(transactionServiceSpy).validateExpenseLimit(eq(userId), eq(request));
        Mockito.when(limitService.getCurrentLimit(userId)).thenReturn(limit.getMaxExpenseLimit());

        transactionService.addTransaction(userId, request, data);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        Mockito.verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(request.amount(), captor.getValue().getAmount());
        ArgumentCaptor<NotificationMessage> captor2 = ArgumentCaptor.forClass(NotificationMessage.class);
        Mockito.verify(kafkaProducerService, times(1)).sendMessage(captor2.capture());
        assertEquals(EventType.TRANSACTION_ADDED, captor2.getValue().eventType());
    }

    @Test
    public void addTransactionTest_shouldThrowException() {
        UserDto data = new UserDto("test@example.com", "John", "Doe");
        AddTransactionRequest request = new AddTransactionRequest(BigDecimal.valueOf(1000), TransactionType.EXPENSE,
                "TestCategory", "Test transaction");

        Category category = new Category();
        category.setName("TestCategory");
        category.setUserId(userId);

        Limit limit = new Limit();
        limit.setMaxExpenseLimit(BigDecimal.valueOf(2000));

        Mockito.doNothing().when(transactionServiceSpy).validateExpenseLimit(eq(userId), eq(request));
        Mockito.when(limitService.getCurrentLimit(userId)).thenReturn(limit.getMaxExpenseLimit());

        assertThrows(CategoryNotFoundException.class, () -> transactionService.addTransaction(userId, request, data));
    }


}
