package org.example;

import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddLimitRequest;
import org.example.entity.Limit;
import org.example.entity.RecurringFrequency;
import org.example.exception.LimitNotFoundException;
import org.example.service.LimitService;
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
public class LimitIntegrationTest {

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
    private LimitService limitService;

    @MockBean
    private UserClient userClient;

    private final UUID userId = UUID.randomUUID();
    private final String token = "Bearer token";


    @BeforeEach
    public void setUp() {
        Mockito.when(userClient.getUserByToken(eq(token))).thenReturn(userId);
    }

    @Test
    public void getUserLimitsTest_shouldReturnLimits() throws Exception {
        Limit limit = mock(Limit.class);

        Mockito.when(limitService.getUsersLimits(userId)).thenReturn(List.of(limit));

        mockMvc.perform(get("/api/v1/limits/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maxExpenseLimit").value(limit.getMaxExpenseLimit()));
    }

    @Test
    public void addLimitTest_shouldAddLimit() throws Exception {
        UserDto data = mock(UserDto.class);

        Mockito.doNothing().when(limitService).addLimit(eq(userId), any(AddLimitRequest.class), eq(data));

        mockMvc.perform(post("/api/v1/limits/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"maxExpenseLimit\": \"25000\", \"frequency\": \"MONTHLY\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Limit added"));
    }

    @Test
    public void deleteLimitTest_shouldDeleteLimit() throws Exception {

        Mockito.doNothing().when(limitService).deleteLimit(eq(userId), any(RecurringFrequency.class));

        mockMvc.perform(delete("/api/v1/limits/delete")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"frequency\": \"MONTHLY\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Limit deleted"));
    }

    @Test
    public void deleteLimitTest_shouldThrowException() throws Exception {

        Mockito.doThrow(new LimitNotFoundException("Limit not found"))
                .when(limitService).deleteLimit(eq(userId), any(RecurringFrequency.class));

        mockMvc.perform(delete("/api/v1/limits/delete")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"frequency\": \"MONTHLY\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Произошла ошибка: Limit not found"));
    }

}
