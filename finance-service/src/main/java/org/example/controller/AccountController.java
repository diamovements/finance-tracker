package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.BalanceRequest;
import org.example.dto.response.AccountDataResponse;
import org.example.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final UserClient userClient;
    private final AccountService accountService;
    private final static String TOKEN = "Authorization";

    @GetMapping
    public AccountDataResponse getCurrentData(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        UserDto data = userClient.getUserData(token);
        return new AccountDataResponse(
                data.name(), data.surname(),
                accountService.calculateExpenses(userId),
                accountService.calculateIncomes(userId),
                accountService.getCurrentBalance(userId),
                accountService.getCurrentLimit(userId));
    }

    @PostMapping("/balance")
    public AccountDataResponse updateBalance(@RequestHeader(TOKEN) String token, @RequestBody BalanceRequest request) {
        UUID userId = userClient.getUserByToken(token);
        accountService.updateBalance(userId, request.balance());
        log.info("Balance updated: {}", request.balance());
        UserDto data = userClient.getUserData(token);
        return new AccountDataResponse(
                data.name(), data.surname(),
                accountService.calculateExpenses(userId),
                accountService.calculateIncomes(userId),
                accountService.getCurrentBalance(userId),
                accountService.getCurrentLimit(userId));
    }

}
