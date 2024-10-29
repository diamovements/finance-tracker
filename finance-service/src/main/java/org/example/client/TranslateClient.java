package org.example.client;

import org.example.dto.response.TranslateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "translateClient", url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl={sourceLanguage}&tl={targetLanguage}&dt=t&q={word}")
//public interface TranslateClient {
//    @GetMapping
//    TranslateResponse translateQuote(@RequestParam("sl") String sourceLanguage,
//                                     @RequestParam("tl") String targetLanguage,
//                                     @RequestParam("q") String word);
//}
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
