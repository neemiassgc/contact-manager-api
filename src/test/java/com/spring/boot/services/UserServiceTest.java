package com.spring.boot.services;

import com.spring.boot.entities.User;
import static org.assertj.core.api.Assertions.*;

import com.spring.boot.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_return_a_user_by_its_username() {
        final User user = userService.findByUsername("robert");

        assertThat(user).isNotNull();
        assertThat(user).extracting("username").isEqualTo("robert");
    }

    @Test
    void should_throw_an_error_when_user_does_not_exist() {
        final Throwable throwable = catchThrowable(() -> userService.findByUsername("fred"));

        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) throwable).getReason()).isEqualTo("User not found");
        assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
    }

    @Test
    void should_create_a_new_user_successfully() {
        final User newUser = new User("Kevin");

        userService.create(newUser);

        final List<User> actualUsers = userRepository.findAll();

        assertThat(actualUsers).hasSize(3);
    }
}
