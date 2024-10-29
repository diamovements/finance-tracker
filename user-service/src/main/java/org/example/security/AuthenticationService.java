package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.SignInRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.response.AuthenticationResponse;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse signUp(SignUpRequest request) {
        User user = User.builder()
                .name(request.name())
                .surname(request.surname())
                .email(request.email())
                .role(Role.ROLE_USER)
                .password(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);
        log.info("User saved: {}", user.getEmail());
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    @Transactional
    public AuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new IllegalArgumentException("Неверный пароль или почта"));
        log.info("User role: {}", user.getRole());
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    @Transactional
    public boolean isAuthorized(String token) {
        return !jwtService.isTokenExpired(token);
    }

    @Transactional
    public boolean isAdmin(String token) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        log.info("User role: {}", user.getRole());
        return user.getRole().equals(Role.ROLE_ADMIN);
    }
}
