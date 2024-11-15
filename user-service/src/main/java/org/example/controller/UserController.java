package org.example.controller;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserDto;
import org.example.exception.ExpiredTokenException;
import org.example.security.JwtService;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final static String TOKEN = "Authorization";


    @GetMapping
    public UserDto getUserData(@RequestHeader(TOKEN) String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        log.debug("Received token: {}", token);
        String email = jwtService.extractUsername(token);
        log.debug("Extracted email: {}", email);
        return userService.getUser(email);
    }

    @GetMapping("/id")
    public UUID getUserId(@RequestHeader(TOKEN) String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        log.debug("Received token: {}", token);
        String email = jwtService.extractUsername(token);
        log.debug("Extracted email: {}", email);
        return userService.getUserId(email);
    }
}
