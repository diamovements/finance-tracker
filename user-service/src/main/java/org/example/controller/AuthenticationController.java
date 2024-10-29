package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.request.SignInRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.response.AuthenticationResponse;
import org.example.security.AuthenticationService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final static String TOKEN = "token";

    @PostMapping("/signup")
    public AuthenticationResponse signUp(@RequestBody SignUpRequest request) {
        String token = authenticationService.signUp(request).token();
        return new AuthenticationResponse(token);
    }

    @PostMapping("/signin")
    public AuthenticationResponse signIn(@RequestBody SignInRequest request) {
        String token = authenticationService.signIn(request).token();
        return new AuthenticationResponse(token);
    }

    @GetMapping()
    public boolean isAuthorized(@RequestHeader(TOKEN) String token) {
        return authenticationService.isAuthorized(token);
    }

    @GetMapping("/isadmin")
    public boolean isAdmin(@RequestHeader(TOKEN) String token) {
        return authenticationService.isAdmin(token);
    }
}
