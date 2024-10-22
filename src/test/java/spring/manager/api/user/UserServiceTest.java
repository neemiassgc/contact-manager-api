package spring.manager.api.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataJpaTest
@Import({UserServiceImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_return_a_user_by_its_id() {
        final User user = userService.findById("auth0|94afd9e7294a59e73e6abfbd");

        assertThat(user).isNotNull();
        assertThat(user).extracting("username").isEqualTo("robert");
    }

    @Test
    void should_throw_an_error_when_user_does_not_exist() {
        final Throwable throwable = catchThrowable(() -> userService.findById("auth0|adea243241c3754b349213d6"));

        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) throwable).getReason()).isEqualTo("User not found");
        assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
    }

    @Test
    void should_create_a_new_user_successfully() {
        final User newUser = new User("auth0|e721524323da766879dfce8b", "Kevin");

        userService.create(newUser);

        final List<User> actualUsers = userRepository.findAll();

        assertThat(actualUsers).hasSize(3);
    }

    @Test
    void should_throw_an_error_when_trying_to_create_a_user_that_already_exists() {
        final User newUser = new User("auth0|3baa9bc92c9c5decbda32f76", "joe");

        final Throwable throwable = catchThrowable(() -> userService.create(newUser));

        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) throwable).getReason()).isEqualTo("User already exists");
        assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(400));
    }

    @Test
    void should_retrieve_a_user_by_their_username_successfully() {
        final User user = userService.findByUsername("robert");

        assertThat(user).isNotNull();
    }

    @Test
    void should_throw_an_exception_when_retrieving_a_user_that_does_not_exist() {
        final Throwable throwable = catchThrowable(() -> userService.findByUsername("Cris"));

        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) throwable).getReason()).isEqualTo("User not found");
        assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
    }
}
