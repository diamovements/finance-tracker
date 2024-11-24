package org.example;

import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class AccountIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserClient userClient;

    private final UUID userId = UUID.randomUUID();
    private final String token = "Bearer token";


    @BeforeEach
    public void setUp() {
        Mockito.when(userClient.getUserByToken(eq(token))).thenReturn(userId);
    }

    @Test
    public void getCurrentDataTest_shouldReturnAccountData() throws Exception {
        UserDto data = mock(UserDto.class);

        Mockito.when(userClient.getUserData(token)).thenReturn(data);
        Mockito.when(accountService.calculateExpenses(userId)).thenReturn(BigDecimal.valueOf(25000));
        Mockito.when(accountService.calculateIncomes(userId)).thenReturn(BigDecimal.valueOf(25000));
        Mockito.when(accountService.getCurrentBalance(userId)).thenReturn(BigDecimal.valueOf(30000));
        Mockito.when(accountService.getCurrentLimit(userId)).thenReturn(BigDecimal.valueOf(90000));

        mockMvc.perform(get("/api/v1/account")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses").value(BigDecimal.valueOf(25000)))
                .andExpect(jsonPath("$.balance").value(BigDecimal.valueOf(30000)));
    }

    @Test
    public void updateBalanceTest_shouldUpdateBalance() throws Exception {
        UserDto data = mock(UserDto.class);
        BigDecimal currentBalance = BigDecimal.valueOf(90000);

        Mockito.when(accountService.getCurrentBalance(userId)).thenReturn(currentBalance);
        Mockito.when(accountService.calculateExpenses(userId)).thenReturn(BigDecimal.valueOf(25000));
        Mockito.when(accountService.calculateIncomes(userId)).thenReturn(BigDecimal.valueOf(25000));
        Mockito.when(userClient.getUserData(token)).thenReturn(data);
        Mockito.when(accountService.getCurrentLimit(userId)).thenReturn(BigDecimal.valueOf(90000));

        mockMvc.perform(post("/api/v1/account/balance")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"balance\": 95000}"))
                .andExpect(status().isOk());
    }

}
