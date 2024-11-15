package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "translateClient", url = "https://translate.googleapis.com")
public interface TranslateClient {
    @GetMapping("/translate_a/single")
    List<Object> translateQuote(
            @RequestParam("client") String client,
            @RequestParam("sl") String sourceLanguage,
            @RequestParam("tl") String targetLanguage,
            @RequestParam("dt") String dt,
            @RequestParam("q") String word
    );
}
