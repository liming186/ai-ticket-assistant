package com.example.aiticketassistant;

import com.example.aiticketassistant.infrastructure.config.AssistantProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AssistantProperties.class)
public class AiTicketAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiTicketAssistantApplication.class, args);
    }
}
