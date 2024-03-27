package com.spring.boot.entities.projections;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class SimpleUser {

    @Size(min = 3, max = 20, message = "'username' must be between 3 and 20 long")
    @NotNull(message = "'username' must not be null")
    private final String username;

    @Pattern(
        regexp = "^(https?)://[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}(/[/a-zA-Z0-9._-]+)*$",
        message = "'avatarUri' must be a valid Url"
    )
    private final String avatarUri;

    public SimpleUser(final String username) {
        this(username, null);
    }
}
