package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuoteResponse(
        @JsonProperty("quote")
        String quote,
        @JsonProperty("author")
        String author) { }
