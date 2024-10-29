package org.example.client;

import org.example.dto.response.QuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "quotesClient", url = "https://api.api-ninjas.com/v1/quotes")
public interface QuotesClient {

    @GetMapping()
    List<QuoteResponse> getQuote(@RequestHeader("X-Api-Key") String apiKey,
                                @RequestParam("category") String category);
}
