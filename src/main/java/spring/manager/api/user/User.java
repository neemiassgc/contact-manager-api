package spring.manager.api.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public final class User {

    @Id
    @Column(name = "user_id", length = 30)
    private String id;

    @Column(length = 20, nullable = false, unique = true)
    private String username;
}