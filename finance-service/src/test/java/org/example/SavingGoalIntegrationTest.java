package org.example;

import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddGoalRequest;
import org.example.entity.SavingGoal;
import org.example.exception.GoalNotFoundException;
import org.example.service.SavingGoalService;
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
public class SavingGoalIntegrationTest {

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
    private SavingGoalService goalService;

    @MockBean
    private UserClient userClient;

    private final UUID userId = UUID.randomUUID();
    private final String token = "Bearer token";


    @BeforeEach
    public void setUp() {
        Mockito.when(userClient.getUserByToken(eq(token))).thenReturn(userId);
    }

    @Test
    public void getAllGoals_shouldReturnGoals() throws Exception {
        SavingGoal goal = mock(SavingGoal.class);
        Mockito.when(goalService.getUsersGoals(userId)).thenReturn(List.of(goal));

        mockMvc.perform(get("/api/v1/saving-goals/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].goalAmount").value(goal.getGoalAmount()));
    }

    @Test
    public void addGoalTest_shouldAddGoal() throws Exception {
        UserDto data = mock(UserDto.class);

        Mockito.doNothing().when(goalService).addGoal(eq(userId), any(AddGoalRequest.class), eq(data));

        mockMvc.perform(post("/api/v1/saving-goals/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": \"25000\", \"endDate\": \"2024-12-31\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal added"));
    }

    @Test
    public void deleteLimitTest_shouldDeleteLimit() throws Exception {

        Mockito.doNothing().when(goalService).deleteGoal(eq(userId));

        mockMvc.perform(delete("/api/v1/saving-goals/delete")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal deleted"));
    }

    @Test
    public void deleteGoalTest_shouldThrowException() throws Exception {

        Mockito.doThrow(new GoalNotFoundException("Goal not found"))
                .when(goalService).deleteGoal(eq(userId));

        mockMvc.perform(delete("/api/v1/saving-goals/delete")
                .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Произошла ошибка: Goal not found"));
    }
}
