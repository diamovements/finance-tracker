package org.example.dto.request;

public record TranslateRequest(String inputString, String sourceLang, String targetLang) {
}
