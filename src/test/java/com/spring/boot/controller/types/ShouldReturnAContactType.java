package com.spring.boot.controller.types;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public final class ShouldReturnAContactType {

    private final String user;
    private final UUID contactId;
    private final int phoneNumberSize;
    private final int addressesSize;
    private final String contactName;
}