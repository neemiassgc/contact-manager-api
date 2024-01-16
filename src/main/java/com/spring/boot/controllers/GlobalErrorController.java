package com.spring.boot.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@ControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> resolveResponseStatusException(final ResponseStatusException rse) {
        return ResponseEntity
            .status(rse.getStatusCode())
            .contentType(MediaType.TEXT_PLAIN)
            .body(Objects.requireNonNullElseGet(rse.getReason(), () -> "No reasons"));
    }
}
