package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.UserClient;
import org.example.dto.request.AddTransactionRequest;
import org.example.dto.response.TransactionResponse;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.exception.LimitExceedException;
import org.example.service.LimitService;
import org.example.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final static String TOKEN = "Authorization";
    private final UserClient userClient;

    @PostMapping("/add")
    public ResponseEntity<String> addTransaction(
            @RequestHeader(TOKEN) String token,
            @RequestBody AddTransactionRequest request) {

        UUID userId = userClient.getUserByToken(token);
        try {
            transactionService.addTransaction(userId, request);
        } catch (LimitExceedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Transaction added successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        List<Transaction> transactions = transactionService.getAllTransactions(userId);

        List<TransactionResponse> response = transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getAmount(),
                        transaction.getCategory().getName(),
                        transaction.getTransactionType(),
                        transaction.getDescription()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-expense")
    public ResponseEntity<BigDecimal> calculateTotalExpense(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        BigDecimal totalExpense = transactionService.calculateTotalExpense(userId);
        return ResponseEntity.ok(totalExpense);
    }


    @GetMapping("/total-income")
    public ResponseEntity<BigDecimal> calculateTotalIncome(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        BigDecimal totalIncome = transactionService.calculateTotalIncome(userId);
        return ResponseEntity.ok(totalIncome);
    }
}

