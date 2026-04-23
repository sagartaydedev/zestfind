package com.zestfind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZestFindApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZestFindApplication.class, args);
        System.out.println("✅ ZestFind Started! Visit: http://localhost:8080");
    }
}