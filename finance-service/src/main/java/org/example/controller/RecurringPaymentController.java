package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddPaymentRequest;
import org.example.entity.RecurringPayment;
import org.example.service.RecurringPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recurring-payments")
public class RecurringPaymentController {

    private final RecurringPaymentService paymentService;
    private final static String TOKEN = "Authorization";
    private final UserClient userClient;

    @GetMapping("/all")
    public List<RecurringPayment> getAllPayments(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        return paymentService.getAllPayments(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPayment(@RequestHeader(TOKEN) String token, @RequestBody AddPaymentRequest request) {
        UUID userId = userClient.getUserByToken(token);
        UserDto data = userClient.getUserData(token);
        paymentService.addPayment(userId, request, data);
        return ResponseEntity.ok("Payment added");
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deletePayment(@RequestHeader(TOKEN) String token, @PathVariable("name") String name) {
        UUID userId = userClient.getUserByToken(token);
        paymentService.deletePayment(userId, name);
        return ResponseEntity.ok("Payment deleted");
    }
}
