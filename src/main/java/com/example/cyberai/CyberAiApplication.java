package com.example.cyberai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class CyberAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CyberAiApplication.class, args);
        System.out.println("🚀 Cyber-AI Sentinel Started Successfully!");
        System.out.println("📍 Access at: http://localhost:8080");
        System.out.println("🤖 AI Assistant Ready with Groq API");
    }
}