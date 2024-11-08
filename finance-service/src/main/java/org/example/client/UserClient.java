package org.example.client;

import org.example.config.FeignConfig;
import org.example.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${user-service.url}", configuration = FeignConfig.class)
public interface UserClient {
    static final String TOKEN = "Authorization";

    @GetMapping("/id")
    UUID getUserByToken(@RequestHeader(TOKEN) String token);

    @GetMapping
    UserDto getUserData(@RequestHeader(TOKEN) String token);

}
