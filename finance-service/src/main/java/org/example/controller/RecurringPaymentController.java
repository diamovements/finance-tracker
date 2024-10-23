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
@RequestMapping("/api/v1/recurring-payments")
public class RecurringPaymentController {

    private final RecurringPaymentService paymentService;

    @GetMapping("/all")
    public List<RecurringPayment> getAllPayments(@RequestParam("userId") UUID userId) {
        return paymentService.getAllPayments(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPayment(@RequestParam("userId") UUID userId,
                                             @RequestParam("name") String name,
                                             @RequestParam("amount") BigDecimal amount,
                                             @RequestParam("startDate") LocalDate startDate,
                                             @RequestParam("frequency") RecurringFrequency frequency) {
        paymentService.addPayment(userId, name, amount, startDate, frequency);
        return ResponseEntity.ok("Payment added");
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deletePayment(@RequestParam("userId") UUID userId, @PathVariable("name") String name) {
        paymentService.deletePayment(userId, name);
        return ResponseEntity.ok("Payment deleted");
    }
}
