package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddGoalRequest;
import org.example.entity.SavingGoal;
import org.example.service.SavingGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/saving-goals")
public class SavingGoalController {
    private final SavingGoalService savingGoalService;
    private final static String TOKEN = "Authorization";
    private final UserClient userClient;

    @GetMapping("/all")
    public List<SavingGoal> getAllGoals(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        return savingGoalService.getUsersGoals(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addGoal(@RequestHeader(TOKEN) String token, @RequestBody AddGoalRequest request) {
        UUID userId = userClient.getUserByToken(token);
        UserDto data = userClient.getUserData(token);
        savingGoalService.addGoal(userId, request, data);
        return ResponseEntity.ok("Goal added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGoal(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        savingGoalService.deleteGoal(userId);
        return ResponseEntity.ok("Goal deleted");
    }
}
