package org.example.controller;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/all")
    public List<SavingGoal> getAllGoals(@RequestParam("userId") UUID userId) {
        return savingGoalService.getUsersGoals(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addGoal(@RequestParam("userId") UUID userId,
                                          @RequestParam("goalAmount") BigDecimal goalAmount,
                                          @RequestParam("endDate") LocalDate endDate) {
        savingGoalService.addGoal(userId, goalAmount, endDate);
        return ResponseEntity.ok("Goal added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGoal(@RequestParam("userId") UUID userId) {
        savingGoalService.deleteGoal(userId);
        return ResponseEntity.ok("Goal deleted");
    }
}
