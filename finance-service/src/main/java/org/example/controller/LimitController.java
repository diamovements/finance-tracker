package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/limits")
@RequiredArgsConstructor
public class LimitController {
    private final LimitService limitService;

    @GetMapping("/all")
    public List<Limit> getUserLimits(@RequestParam("userId") UUID userId) {
        return limitService.getUsersLimits(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addLimit(@RequestParam("userId") UUID userId, @RequestParam("maxExpense") BigDecimal maxExpense, @RequestParam("frequency") RecurringFrequency frequency) {
        limitService.addLimit(userId, maxExpense, frequency);
        return ResponseEntity.ok("Limit added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteLimit(@RequestParam("userId") UUID userId, @RequestParam("frequency") RecurringFrequency frequency) {
        limitService.deleteLimit(userId, frequency);
        return ResponseEntity.ok("Limit deleted");
    }
}
