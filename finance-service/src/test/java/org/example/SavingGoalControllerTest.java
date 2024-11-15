package org.example;

import org.example.entity.SavingGoal;
import org.example.service.SavingGoalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class SavingGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavingGoalService savingGoalService;

    @Test
    public void getAllGoalsTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        List<SavingGoal> goals = new ArrayList<>();
        SavingGoal goal = new SavingGoal();
        goal.setGoalAmount(BigDecimal.valueOf(10000.0));
        goal.setEndDate(LocalDate.now());
        goals.add(goal);

        Mockito.when(savingGoalService.getUsersGoals(userId)).thenReturn(goals);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/saving-goals/all")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addGoalTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/saving-goals/add")
                        .param("userId", userId.toString())
                        .param("goalAmount", "10000.0")
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteGoalTest() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/saving-goals/delete")
                        .param("userId", userId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
