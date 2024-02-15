package com.spring.boot.controllers;

import com.spring.boot.error.ViolationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ViolationResponse> resolveResponseStatusException(final MethodArgumentNotValidException manve) {
        final ViolationResponse violationResponse = manve
            .getFieldErrors()
            .stream()
            .collect(ViolationResponse::new, ViolationResponse::putFieldViolation, (a, b) -> {});

        return ResponseEntity.badRequest().body(violationResponse);
    }
}
