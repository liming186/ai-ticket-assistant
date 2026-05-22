package com.example.aiticketassistant.application.agent;

public interface AiClientPort {
    AiResponse complete(AiRequest request);
}
