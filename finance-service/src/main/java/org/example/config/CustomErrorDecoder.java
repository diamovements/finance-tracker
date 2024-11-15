package org.example.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.UNAUTHORIZED.value()) {
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен истек или недействителен");
        }
        return new ErrorDecoder.Default().decode(methodKey, response);
    }
}
