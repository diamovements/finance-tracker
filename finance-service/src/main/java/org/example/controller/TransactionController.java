package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddTransactionRequest;
import org.example.dto.response.TransactionResponse;
import org.example.entity.Transaction;
import org.example.exception.LimitExceedException;
import org.example.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
        UserDto data = userClient.getUserData(token);
        try {
            transactionService.addTransaction(userId, request, data);
        } catch (LimitExceedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Transaction added");
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @RequestHeader(TOKEN) String token,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size) {

        UUID userId = userClient.getUserByToken(token);
        Page<Transaction> transactionsPage = transactionService.getAllTransactions(userId, page, size);

        List<TransactionResponse> response = transactionsPage.stream()
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

