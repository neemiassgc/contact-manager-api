package com.spring.boot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 20, unique = true)
    @Setter
    private String username;

    private String avatarUri;

    public User(final String username) {
        this.username = username;
    }
}
