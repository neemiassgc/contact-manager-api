package contact.manager.api.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static contact.manager.api.misc.TestResources.*;

@DataJpaTest
@Import({UserServiceImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Nested
    public class Create {

        @Test
        @DisplayName("Should create a new user successfully")
        void shouldCreateANewUserSuccessfully() {
            User newUser = new User("1933245856", "Elise");

            userService.create(newUser);

            int actualUsersCount = userRepository.findAll().size();
            assertThat(actualUsersCount).isEqualTo(4);
        }

        @Test
        @DisplayName("When creating a user that already exists then should fail with an exception")
        void whenCreatingAUserThatAlreadyExists_thenShouldFailWithAnException() {
            User existingUser = new User(Users.JOE.id(), "Joe");

            Throwable throwable = catchThrowable(() -> userService.create(existingUser));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User already exists");
        }
    }

    @Nested
    public class FindById {

        @Test
        @DisplayName("When provided an id then should return a user")
        void whenProvidedAnId_thenShouldReturnAUser() {
            String userId = Users.ROBERT.id();

            User actualUser = userService.findById(userId);

            assertThat(actualUser).extracting(User::getUsername).isEqualTo("robert");
        }

        @Test
        @DisplayName("When provided a non-existing id then should throw an exception")
        void whenProvidedANonExistingId_thenShouldThrowAnException() {
            String nonExistingId = "89012347234";

            Throwable throwable = catchThrowable(() -> userService.findById(nonExistingId));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User not found");
        }
    }

    @Nested
    public class FindByUsername {

        @Test
        @DisplayName("When provided a username then should return a user")
        void whenProvidedAUsername_thenShouldReturnAUser() {
            String username = "robert";

            User actualUser = userService.findByUsername(username);

            assertThat(actualUser).extracting(User::getUsername).isEqualTo("robert");
        }

        @Test
        @DisplayName("When provided a non-existing username then should throw an exception")
        void whenProvidedANonExistingUsername_thenShouldThrowAnException() {
            String nonExistingUsername = "jeff";

            Throwable throwable = catchThrowable(() -> userService.findById(nonExistingUsername));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User not found");
        }
    }
}
