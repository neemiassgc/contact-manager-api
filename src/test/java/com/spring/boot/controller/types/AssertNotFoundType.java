package com.spring.boot.controller.types;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Builder
@Getter
public final class AssertNotFoundType {

    private final String user;
    private final HttpMethod httpMethod;
    private final UUID contactId;
    private final String errorMessage;
    private final HttpStatus httpStatus;
}
