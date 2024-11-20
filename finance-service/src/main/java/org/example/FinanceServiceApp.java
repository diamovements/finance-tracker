package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Map;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class FinanceServiceApp {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n", envName, env.get(envName));
            System.setProperty(envName, env.get(envName));
        }
        SpringApplication.run(FinanceServiceApp.class, args);
    }
}
