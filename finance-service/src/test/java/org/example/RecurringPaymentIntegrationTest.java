package org.example;

import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddPaymentRequest;
import org.example.entity.RecurringPayment;
import org.example.exception.PaymentNotFoundException;
import org.example.service.RecurringPaymentService;
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


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class RecurringPaymentIntegrationTest {

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
    private RecurringPaymentService paymentService;

    @MockBean
    private UserClient userClient;

    private final UUID userId = UUID.randomUUID();
    private final String token = "Bearer token";


    @BeforeEach
    public void setUp() {
        Mockito.when(userClient.getUserByToken(eq(token))).thenReturn(userId);
    }

    @Test
    public void getAllPaymentsTest_shouldReturnPayments() throws Exception {
        RecurringPayment payment = mock(RecurringPayment.class);

        Mockito.when(paymentService.getAllPayments(userId)).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/v1/recurring-payments/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(payment.getName()));
    }

    @Test
    public void addPaymentTest_shouldAddPayment() throws Exception {
        UserDto data = mock(UserDto.class);

        Mockito.doNothing().when(paymentService).addPayment(eq(userId), any(AddPaymentRequest.class), eq(data));

        mockMvc.perform(post("/api/v1/recurring-payments/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Проездной\", \"amount\": 1000, \"startDate\": \"2024-11-12\", \"recurringFrequency\": \"DAILY\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment added"));
    }

    @Test
    public void deletePaymentTest_shouldDeletePayment() throws Exception {
        RecurringPayment payment = mock(RecurringPayment.class);
        String name = "Проездной";

        Mockito.when(payment.getName()).thenReturn(name);
        Mockito.doNothing().when(paymentService).deletePayment(eq(userId), eq(name));

        mockMvc.perform(delete("/api/v1/recurring-payments/delete/{name}", payment.getName())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment deleted"));
    }

    @Test
    public void deletePaymentTest_shouldThrowException() throws Exception {
        String name = "Проездной";

        Mockito.doThrow(new PaymentNotFoundException("Payment not found"))
                .when(paymentService).deletePayment(eq(userId), any(String.class));

        mockMvc.perform(delete("/api/v1/recurring-payments/delete/{name}", name)
                        .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Произошла ошибка: Payment not found"));
    }
}
