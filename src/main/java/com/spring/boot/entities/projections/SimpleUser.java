package com.spring.boot.entities.projections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class SimpleUser {

    private final String username;
    private final String avatarUri;

    public SimpleUser(final String username) {
        this(username, null);
    }
}
