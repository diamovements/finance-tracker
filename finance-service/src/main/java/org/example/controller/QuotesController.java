package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.response.QuoteResponse;
import org.example.service.QuotesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quotes")
public class QuotesController {

    private final QuotesService quotesService;

    @GetMapping()
    public QuoteResponse getQuotes(@RequestParam("category") String category) {
        return quotesService.getQuote(category);
    }
}
