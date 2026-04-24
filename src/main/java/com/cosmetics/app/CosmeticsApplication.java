package com.cosmetics.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CosmeticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CosmeticsApplication.class, args);
        System.out.println("🚀 Server running at http://localhost:8080");
    }
}