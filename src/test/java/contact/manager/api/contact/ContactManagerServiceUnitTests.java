package contact.manager.api.contact;

import contact.manager.api.misc.TestResources;
import contact.manager.api.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static contact.manager.api.misc.TestResources.once;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class ContactManagerServiceUnitTests {

    @MockBean
    private ContactRepository contactRepository;

    @MockBean
    private UserService userService;

    private ContactManagerService contactManagerServiceUnderTest;

    @BeforeEach
    void beforeEach() {
        contactManagerServiceUnderTest = new ContactManagerServiceImpl(contactRepository, userService);
    }

    @Nested
    public class FindAll {

        @Test
        void shouldReturnAllOfTheContactsAvailable() {
            when(contactRepository.findAll()).thenReturn(TestResources.getAFewContacts(10));

            List<Contact> actualContacts = contactManagerServiceUnderTest.findAll();

            assertThat(actualContacts).hasSize(10);

            verify(contactRepository, once()).findAll();
            verifyNoMoreInteractions(contactRepository);
        }
    }

    @Nested
    public class FindAllByUserId {

        @Test
        @DisplayName("Given a valid user id then should return all of the contacts owned by the user")
        void whenUserIdIsValid_thenShouldReturnAllOfTheContactsOwnedByTheUser() {
            String robertId = TestResources.idForRobert();
            when(userService.findById(eq(robertId))).thenReturn(TestResources.getMockedUser());
            when(contactRepository.findAllByUserId(eq(robertId))).thenReturn(TestResources.getContactsForRobert());

            List<Contact> actualContacts = contactManagerServiceUnderTest.findAllByUserId(robertId);

            assertThat(actualContacts).hasSize(4);

            verify(userService, once()).findById(eq(robertId));
            verify(contactRepository, once()).findAllByUserId(eq(robertId));
            verifyNoMoreInteractions(userService, contactRepository);
        }

        @Test
        @DisplayName("Given an invalid user id then should throw ResponseStatusException NOT FOUND")
        void whenUserIdIsInvalid_thenShouldThrowAnException() {
            String robertId = TestResources.idForRobert();
            when(userService.findById(eq(robertId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findAllByUserId(robertId));

            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User not found");

            verify(userService, once()).findById(eq(robertId));
            verifyNoMoreInteractions(userService, contactRepository);
        }
    }
}