package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
    static final String TOKEN = "Authorization";

    @GetMapping("/id")
    UUID getUserByToken(@RequestHeader(TOKEN) String token);

}
