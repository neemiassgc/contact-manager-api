package contact.manager.api.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static contact.manager.api.misc.TestResources.getMockedUser;
import static contact.manager.api.misc.TestResources.once;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class UserServiceTest {

    @MockBean
    public UserRepository userRepository;

    public UserService userServiceUnderTest;

    @BeforeEach
    void beforeEach() {
        userServiceUnderTest = new UserServiceImpl(userRepository);
    }

    @Nested
    public class Create {

        @Test
        @DisplayName("Should create a new user successfully")
        void shouldCreateANewUserSuccessfully() {
            User targetUser = getMockedUser();
            when(userRepository.findById(eq(targetUser.getId()))).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class)))
                .then(invocation -> invocation.getArgument(0));

            userServiceUnderTest.create(targetUser);

            verify(userRepository, once()).findById(eq(targetUser.getId()));
            verify(userRepository, once()).save(any(User.class));
        }

        @Test
        @DisplayName("When creating a user that already exists then should fail with an exception")
        void whenCreatingAUserThatAlreadyExists_thenShouldFailWithAnException() {
            User targetUser = getMockedUser();
            when(userRepository.findById(eq(targetUser.getId())))
                .thenReturn(Optional.of(targetUser));

            Throwable throwable = catchThrowable(() -> userServiceUnderTest.create(targetUser));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User already exists");

            verify(userRepository, once()).findById(eq(targetUser.getId()));
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    public class FindById {

        @Test
        @DisplayName("When provided an id then should return a user")
        void whenProvidedAnId_thenShouldReturnAUser() {
            User targetUser = getMockedUser();
            when(userRepository.findById(eq(targetUser.getId()))).thenReturn(Optional.of(targetUser));

            User actualUser = userServiceUnderTest.findById(targetUser.getId());

            assertThat(actualUser).extracting(User::getUsername).isEqualTo("Jessi");

            verify(userRepository, once()).findById(eq(targetUser.getId()));
        }

        @Test
        @DisplayName("When provided a non-existing id then should throw an exception")
        void whenProvidedANonExistingId_thenShouldThrowAnException() {
            User targetUser = getMockedUser();
            when(userRepository.findById(eq(targetUser.getId())))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            Throwable throwable = catchThrowable(() -> userServiceUnderTest.findById(targetUser.getId()));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User not found");

            verify(userRepository, once()).findById(eq(targetUser.getId()));
        }
    }

    @Nested
    public class FindByUsername {

        @Test
        @DisplayName("When provided a username then should return a user")
        void whenProvidedAUsername_thenShouldReturnAUser() {
            String username = "jessi";
            when(userRepository.findByUsername(eq(username)))
                .thenReturn(Optional.of(getMockedUser()));

            User actualUser = userServiceUnderTest.findByUsername(username);

            assertThat(actualUser).extracting(User::getUsername).isEqualTo("Jessi");

            verify(userRepository, once()).findByUsername(eq(username));
        }

        @Test
        @DisplayName("When provided a non-existing username then should throw an exception")
        void whenProvidedANonExistingUsername_thenShouldThrowAnException() {
            User targetUser = getMockedUser();
            when(userRepository.findById(eq(targetUser.getUsername())))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            Throwable throwable = catchThrowable(() -> userServiceUnderTest.findById(targetUser.getId()));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User not found");

            verify(userRepository, once()).findById(eq(targetUser.getId()));
        }
    }
}
