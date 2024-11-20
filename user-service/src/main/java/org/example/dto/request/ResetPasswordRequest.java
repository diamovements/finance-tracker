package org.example.dto.request;

public record ResetPasswordRequest(String email, String code, String newPassword) { }
