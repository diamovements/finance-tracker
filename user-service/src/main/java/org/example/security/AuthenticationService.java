package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.dto.PasswordResetNotification;
import org.example.dto.request.ResetPasswordRequest;
import org.example.dto.request.SignInRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.response.AuthenticationResponse;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.exception.ExpiredTokenException;
import org.example.repository.UserRepository;
import org.example.service.KafkaProducerService;
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
    private final KafkaProducerService kafkaProducerService;

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
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new IllegalArgumentException("Неверный пароль или почта"));
        log.info("User role: {}", user.getRole());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Transactional
    public boolean isAuthorized(String token) {
        return !jwtService.isTokenExpired(token);
    }

    @Transactional
    public AuthenticationResponse refreshAccessToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new ExpiredTokenException("Refresh токен истек");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        String newAccessToken = jwtService.generateAccessToken(user);
        log.info("New access token: {}", newAccessToken);
        return new AuthenticationResponse(newAccessToken, refreshToken);
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        String confirmationCode = generateConfirmationCode();
        user.setResetCode(confirmationCode);
        userRepository.save(user);
        PasswordResetNotification message = new PasswordResetNotification(confirmationCode, email);
        kafkaProducerService.sendMessage(message);
    }
    private String generateConfirmationCode() {
        return RandomStringUtils.randomNumeric(6);
    }

    public void confirmPasswordReset(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        if (!request.code().equals(user.getResetCode())) {
            throw new IllegalArgumentException("Неверный код подтверждения. Попробуйте ещё раз");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetCode(null);
        userRepository.save(user);
    }
}
