package com.example.aiticketassistant.interfaces.rest;

import com.example.aiticketassistant.domain.shared.DomainException;
import com.example.aiticketassistant.domain.shared.NotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(NotFoundException ex) {
        return body("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler({DomainException.class, IllegalArgumentException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> badRequest(Exception ex) {
        return body("BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> error(Exception ex) {
        return body("INTERNAL_ERROR", "Unexpected server error");
    }

    private Map<String, Object> body(String code, String message) {
        return Map.of("code", code, "message", message, "timestamp", Instant.now());
    }
}
