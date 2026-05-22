package com.example.aiticketassistant.application.agent;

import com.example.aiticketassistant.application.tool.ToolCall;
import com.example.aiticketassistant.domain.ticket.TicketPriority;
import java.util.List;

public record IntentResult(
        IntentType intent,
        String title,
        TicketPriority priority,
        double confidence,
        List<ToolCall> toolCalls) {}
