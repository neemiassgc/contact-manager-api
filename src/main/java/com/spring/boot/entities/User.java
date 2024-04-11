package com.spring.boot.entities;

import com.spring.boot.entities.projections.SimpleUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 20, unique = true)
    @Setter
    private String username;

    @Setter
    private String avatarUri;

    public User(final String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, avatarUri);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof final User thatUser)) return false;
        return
            Objects.equals(id, thatUser.getId()) &&
            Objects.equals(username, thatUser.getUsername()) &&
            Objects.equals(avatarUri, thatUser.getAvatarUri());
    }

    public static User toUser(final SimpleUser simpleUser) {
        final User newUser = new User(simpleUser.getUsername());
        newUser.setAvatarUri(simpleUser.getAvatarUri());
        return newUser;
    }
}
