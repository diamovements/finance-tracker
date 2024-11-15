package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TranslateResponse(
        @JsonProperty("translatedString")
        String translatedString) { }
