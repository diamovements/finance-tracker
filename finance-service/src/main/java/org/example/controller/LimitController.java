package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.UserClient;
import org.example.dto.UserDto;
import org.example.dto.request.AddLimitRequest;
import org.example.dto.request.DeleteLimitRequest;
import org.example.entity.Limit;
import org.example.service.LimitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/limits")
@RequiredArgsConstructor
public class LimitController {
    private final LimitService limitService;
    private final UserClient userClient;
    private final static String TOKEN = "Authorization";

    @GetMapping("/all")
    public List<Limit> getUserLimits(@RequestHeader(TOKEN) String token) {
        UUID userId = userClient.getUserByToken(token);
        return limitService.getUsersLimits(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addLimit(@RequestHeader(TOKEN) String token, @RequestBody AddLimitRequest request) {
        UUID userId = userClient.getUserByToken(token);
        UserDto data = userClient.getUserData(token);
        limitService.addLimit(userId, request, data);
        return ResponseEntity.ok("Limit added");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteLimit(@RequestHeader(TOKEN) String token, @RequestBody DeleteLimitRequest request) {
        UUID userId = userClient.getUserByToken(token);
        limitService.deleteLimit(userId, request.frequency());
        return ResponseEntity.ok("Limit deleted");
    }
}
