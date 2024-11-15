package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.QuotesClient;
import org.example.client.TranslateClient;
import org.example.dto.response.QuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotesService {
    private final QuotesClient quotesClient;
    private final TranslateClient translateClient;
    @Value("${quotes.api-key}")
    private String API_KEY;
    private final static String TARGET_LANG = "ru";
    private final static String SOURCE_LANG = "en";
    private final static String CLIENT = "gtx";
    private final static String DT = "t";



    public QuoteResponse getQuote(String category) {
        QuoteResponse quoteResponse = quotesClient.getQuote(API_KEY, category).get(0);
        String quote = quoteResponse.quote();
        String author = quoteResponse.author();

        List<Object> translationResponse = translateClient.translateQuote(CLIENT, SOURCE_LANG, TARGET_LANG, DT, quote);

        String translatedQuote = "";
        if (!translationResponse.isEmpty() && translationResponse.get(0) instanceof List<?> firstLevelList) {
            if (!firstLevelList.isEmpty() && firstLevelList.get(0) instanceof List<?> secondLevelList) {
                if (!secondLevelList.isEmpty() && secondLevelList.get(0) instanceof String) {
                    translatedQuote = (String) secondLevelList.get(0);
                }
            }
        }

        log.info("quote: {}", quote);
        log.info("translated quote: {}", translatedQuote);

        return new QuoteResponse(translatedQuote, author);
    }

}
