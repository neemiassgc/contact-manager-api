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
import java.util.Optional;
import java.util.UUID;

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
            verifyNoInteractions(contactRepository);
        }
    }

    @Nested
    public class FindByIdWithUser {

        @Test
        @DisplayName("When ContactId does not exist then should throw ResponseStatusException Contact not found")
        public void whenContactIdDoesNotExist_thenShouldThrowAnException() {
            String robertId = TestResources.idForRobert();
            UUID contactId = UUID.fromString("37414529-e28f-47bc-a4fa-99c2aa79ca90");
            when(contactRepository.findById(eq(contactId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findByIdWithUser(contactId, robertId));

            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("Contact not found");

            verify(contactRepository, once()).findById(eq(contactId));
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When a contact does not belong to the user then should throw ResponseStatusException")
        public void whenContactDoesNotBelongToTheUser_thenShouldThrowAnException() {
            String robertId = TestResources.idForRobert();
            UUID contactId = UUID.fromString("37414529-e28f-47bc-a4fa-99c2aa79ca90");
            when(contactRepository.findById(eq(contactId))).thenReturn(Optional.of(TestResources.getFirstContact()));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findByIdWithUser(contactId, robertId));

            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("Contact belongs to another user");

            verify(contactRepository, once()).findById(eq(contactId));
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When provided contactId and userId then should return a contact successfully")
        public void whenProvidedContactIdAndUserId_thenShouldReturnAContactSuccessfully() {
            String robertId = TestResources.idForRobert();
            UUID contactId = UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09");
            when(contactRepository.findById(eq(contactId))).thenReturn(Optional.of(TestResources.getContactById(contactId)));

            Contact actualContact = contactManagerServiceUnderTest.findByIdWithUser(contactId, robertId);

            assertThat(actualContact).isNotNull();

            verify(contactRepository, once()).findById(eq(contactId));
            verifyNoInteractions(userService);
        }
    }

    @Nested
    public class SaveWithUser {

        @Test
        @DisplayName("When contact and userId are provided then should save the contact successfully")
        public void whenProvidedContactAndUserId_thenShouldSaveTheContactSuccessfully() {
            Contact contact = TestResources.getFirstContact();
            String robertId = TestResources.idForRobert();
            when(userService.findById(eq(robertId))).thenReturn(TestResources.getMockedUser());
            doNothing().when(contactRepository).save(any(Contact.class));

            contactManagerServiceUnderTest.saveWithUser(contact, robertId);

            verify(userService, once()).findById(eq(robertId));
            verify(contactRepository, once()).save(any(Contact.class));
        }

        @Test
        @DisplayName("When provided a userId that does not exist then should throw ResponseStatusException User not found")
        public void whenUserIdDoesNotExist_thenShouldThrowAnException() {
            Contact contact = TestResources.getFirstContact();
            String robertId = TestResources.idForRobert();
            when(userService.findById(eq(robertId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.saveWithUser(contact, robertId));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("User not found");

            verify(userService, once()).findById(eq(robertId));
            verifyNoInteractions(contactRepository);
        }
    }

    @Nested
    public class UpdateWithUser {

        @Test
        @DisplayName("When provided a non existing contact then should throw ResponseStatusException Contact not found")
        public void whenProvidedANonExistingContact_whenShouldThrowAnException() {
            String robertId = TestResources.idForRobert();
            Contact contact = TestResources.getFirstContact();
            when(contactRepository.findById(eq(contact.getId())))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.updateWithUser(contact, robertId));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("Contact not found");

            verify(contactRepository, once()).findById(eq(contact.getId()));
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When a contact does not belong to the user then should throw an exception")
        public void whenAContactDoesNotBelongToTheUser_thenShouldThrowAnException() {
            String robertId = TestResources.idForRobert();
            Contact contact = TestResources.getFirstContact();
            when(contactRepository.findById(eq(contact.getId())))
                .thenReturn(Optional.of(TestResources.getFirstContact()));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.updateWithUser(contact, robertId));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("Contact belongs to another user");

            verify(contactRepository, once()).findById(eq(contact.getId()));
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When provided contact and userId then should entirely update the contact successfully")
        public void whenProvidedContactAndUserId_thenShouldUpdateTheContactSuccessfully() {
            String robertId = TestResources.idForRobert();
            UUID contactId = UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09");
            Contact contact = TestResources.getContactById(contactId);
            when(contactRepository.findById(eq(contactId))).thenReturn(Optional.of(contact));
            doNothing().when(contactRepository).save(any(Contact.class));

            contactManagerServiceUnderTest.updateWithUser(contact, robertId);

            verify(contactRepository, once()).findById(eq(contactId));
            verify(contactRepository, once()).save(any(Contact.class));
            verifyNoInteractions(userService);
        }
    }

    @Nested
    public class FindById {

        @Test
        @DisplayName("When a contact is not found then should throw an exception")
        public void whenContactIsNotFound_thenShouldThrowAnException() {
            UUID contactId = UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09");
            when(contactRepository.findById(eq(contactId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

            Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findById(contactId));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("Contact not found");

            verify(contactRepository, once()).findById(eq(contactId));
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When a contact is found then should return it successfully")
        public void whenContactIsFound_thenShouldReturnItSuccessfully() {
            UUID contactId = UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09");
            when(contactRepository.findById(eq(contactId)))
                    .thenReturn(Optional.of(TestResources.getContactById(contactId)));

            Contact actualContact = contactManagerServiceUnderTest.findById(contactId);

            assertThat(actualContact).isNotNull();

            verify(contactRepository, once()).findById(eq(contactId));
            verifyNoInteractions(userService);
        }
    }

    @Nested
    public class DeleteByIdWithUser {

        @Test
        @DisplayName("When provided contactId and userId then should delete a contact successfully")
        public void whenProvidedContactIdAndUserId_thenShouldDeleteAContactSuccessfully() {
            String robertId = TestResources.idForRobert();
            UUID contactId = UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09");
            when(contactRepository.findById(eq(contactId)))
                .thenReturn(Optional.of(TestResources.getContactById(contactId)));
            doNothing().when(contactRepository).deleteById(eq(contactId));

            contactManagerServiceUnderTest.deleteByIdWithUser(contactId, robertId);

            verify(contactRepository, once()).findById(eq(contactId));
            verify(contactRepository, once()).deleteById(contactId);
            verifyNoInteractions(userService);
        }
    }
}