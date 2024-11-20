package org.example;

import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddTransactionRequest;
import org.example.entity.Category;
import org.example.entity.Transaction;
import org.example.service.CategoryService;
import org.example.service.LimitService;
import org.example.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class TransactionIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private LimitService limitService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserClient userClient;

    private final UUID userId = UUID.randomUUID();
    private final String token = "Bearer token";


    @BeforeEach
    public void setUp() {
        Mockito.when(userClient.getUserByToken(eq(token))).thenReturn(userId);
    }

    @Test
    public void getAllTransactionsTest_shouldReturnTransactions() throws Exception {
        Transaction transaction = mock(Transaction.class);
        Category category = new Category();
        category.setName("Рестораны");

        Mockito.when(transaction.getCategory()).thenReturn(category);
        Page<Transaction> pages = new PageImpl<>(List.of(transaction), PageRequest.of(1, 5), 1);

        Mockito.when(transactionService.getAllTransactions(userId, 1, 5)).thenReturn(pages);

        mockMvc.perform(get("/api/v1/transactions/all")
                .header("Authorization", token)
                .param("page", "1")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value(category.getName()));
    }

    @Test
    public void addTransactionTest_shouldAddTransaction() throws Exception {
        UserDto data = mock(UserDto.class);

        Mockito.doNothing().when(transactionService)
                .addTransaction(eq(userId), any(AddTransactionRequest.class), eq(data));

        mockMvc.perform(post("/api/v1/transactions/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": \"25000\", \"transactionType\": \"EXPENSE\", \"category\": \"Рестораны\", \"description\": \"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction added"));
    }

    @Test
    public void calculateTotalExpenseTest_shouldReturnTotalExpense() throws Exception {
        BigDecimal total = BigDecimal.valueOf(25000);

        Mockito.when(transactionService.calculateTotalExpense(userId)).thenReturn(total);

        mockMvc.perform(get("/api/v1/transactions/total-expense")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(total));
    }

    @Test
    public void calculateTotalIncomeTest_shouldReturnTotalIncome() throws Exception {
        BigDecimal total = BigDecimal.valueOf(25000);

        Mockito.when(transactionService.calculateTotalIncome(userId)).thenReturn(total);

        mockMvc.perform(get("/api/v1/transactions/total-income")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(total));
    }
}
