package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/add")
    public ResponseEntity<String> addTransaction(
            @RequestParam("userId") UUID userId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("type") TransactionType type,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("description") String description) {

        transactionService.addTransaction(userId, amount, type, categoryName, description);
        return ResponseEntity.ok("Transaction added successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestParam("userId") UUID userId) {
        List<Transaction> transactions = transactionService.getAllTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/total-expense")
    public ResponseEntity<BigDecimal> calculateTotalExpense(@RequestParam("userId") UUID userId) {
        BigDecimal totalExpense = transactionService.calculateTotalExpense(userId);
        return ResponseEntity.ok(totalExpense);
    }


    @GetMapping("/total-income")
    public ResponseEntity<BigDecimal> calculateTotalIncome(@RequestParam("userId") UUID userId) {
        BigDecimal totalIncome = transactionService.calculateTotalIncome(userId);
        return ResponseEntity.ok(totalIncome);
    }
}

