package contact.manager.api.contact;

import contact.manager.api.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static contact.manager.api.misc.TestResources.*;

@SpringBootTest
@Transactional
public class ContactManagerServiceIntegrationTest {

    @Autowired
    private ContactManagerService contactManagerServiceUnderTest;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    @Nested
    public class FindAll {

        @Test
        @DisplayName("Should return all of the contacts available")
        void shouldReturnAllOfTheContactsAvailable() {
            List<Contact> contactList = contactManagerServiceUnderTest.findAll();

            Assertions.assertThat(contactList).hasSize(7);
            Assertions.assertThat(contactList).extracting(Contact::getPhoneNumberMap).flatMap(Map::values).hasSize(12);
            Assertions.assertThat(contactList).extracting(Contact::getAddressMap).flatMap(Map::values).hasSize(11);
            Assertions.assertThat(contactList).extracting(Contact::getEmailMap).flatMap(Map::values).hasSize(10);
        }
    }

    @Nested
    public class FindAllByUserId {

        @Test
        @DisplayName("Should return all of the contacts for a given user successfully")
        void shouldReturnAllOfTheContactsForAGivenUserSuccessfully() {
            final List<Contact> listOfContacts = contactManagerServiceUnderTest.findAllByUserId(Users.ROBERT.id());

            Assertions.assertThat(listOfContacts).hasSize(4);
            Assertions.assertThat(listOfContacts).extracting(Contact::getName)
                    .containsExactly("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff");
            Assertions.assertThat(listOfContacts).extracting(Contact::getEmailMap).flatExtracting(Map::keySet).hasSize(7);
            Assertions.assertThat(listOfContacts).extracting(Contact::getPhoneNumberMap).flatExtracting(Map::keySet).hasSize(7);
            Assertions.assertThat(listOfContacts).extracting(Contact::getAddressMap).flatExtracting(Map::keySet).hasSize(7);
        }

        @ParameterizedTest
        @ValueSource(strings = {"109mks82m37a9s", "auth0|c7b8835b2947d4bcc799dca5"})
        @DisplayName("When provided an invalid userId then should throw an exception")
        void whenProvidedAnInvalidUserId_thenShouldThrowAnException(String userId) {
            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findAllByUserId(userId));

            assertResponseStatusException(throwable, "User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    public class FindByUserId {

        @Test
        @DisplayName("When provided a contactId then should return a contact of a user successfully")
        void whenProvidedAContactId_thenShouldReturnAContactOfAUserSuccessfully() {
            final UUID gregFromAccountingContactId = UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167");
            final Contact contact = contactManagerServiceUnderTest.findByIdWithUser(gregFromAccountingContactId, Users.JOE.id());

            assertThat(contact).isNotNull();
            assertThat(contact).extracting(Contact::getName).isEqualTo("Greg from accounting");
            assertThat(contact).extracting(Contact::getPhoneNumberMap).satisfies(phoneNumberMap -> {
                assertThat(phoneNumberMap).isNotNull();
                assertThat(phoneNumberMap).hasSize(1);
                assertThat(phoneNumberMap).containsOnly(Map.entry("home", "+3592659480427"));
            });
            assertThat(contact).extracting(Contact::getEmailMap).satisfies(emailMap -> {
                assertThat(emailMap).isNotNull();
                assertThat(emailMap).hasSize(1);
                assertThat(emailMap).containsOnly(Map.entry("main", "sailor.greg99@hotmail.co.jp"));
            });
            assertThat(contact).extracting(Contact::getAddressMap).satisfies(addressMap -> {
                assertThat(addressMap).isNotNull();
                assertThat(addressMap).hasSize(2);
                assertThat(addressMap.keySet()).containsOnly("home", "work");
                assertThat(addressMap.values()).extracting(Address::getCity).containsOnly("Abiko-shi", "Rankoshi-cho Isoya-gun");
            });
        }

        @Test
        @DisplayName("When a contact is not found then should throw an exception Contact not found")
        void whenAContactIsNotFound_thenShouldThrowAnException() {
            final UUID contactId = UUID.fromString("5b530070-5840-4b26-a41b-3d13f5ee79d5");

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findByIdWithUser(contactId, Users.JOE.id()));

            assertResponseStatusException(throwable, "Contact not found", HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("When a contact does not belong to a user then should throw an exception")
        void whenAContactDoesNotBelongToAUser_thenShouldThrowAnException() {
            final UUID contactId = UUID.fromString("84edd1b9-89a5-4107-a84d-435676c2b8f5");

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findByIdWithUser(contactId, Users.JOE.id()));

            assertResponseStatusException(throwable, "Contact belongs to another user", HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    public class SaveWithUser {

        @Test
        @DisplayName("Should save a contact for a user successfully")
        void shouldSaveAContactForAUserSuccessfully() {
            final Contact newContact = new Contact("boss");
            newContact.putPhoneNumber("office", "+1(57)8131-9975");
            newContact.putEmail("business", "main.contact@company.com");
            final Address companyAddress = Address.builder()
                .country("US")
                .street("1767 Heavner Avenue")
                .state("Georgia")
                .city("Carrollton")
                .zipcode("30117")
                .build();
            newContact.putAddress("work", companyAddress);

            contactManagerServiceUnderTest.saveWithUser(newContact, Users.ROBERT.id());

            final List<Contact> listOfContacts = contactRepository.findAll();
            Assertions.assertThat(listOfContacts).hasSize(8);
            Assertions.assertThat(listOfContacts).extracting(Contact::getPhoneNumberMap).flatMap(Map::values).hasSize(13);
            Assertions.assertThat(listOfContacts).extracting(Contact::getAddressMap).flatMap(Map::values).hasSize(12);
            Assertions.assertThat(listOfContacts).extracting(Contact::getEmailMap).flatMap(Map::values).hasSize(11);
        }

        @Test
        @Transactional(propagation = Propagation.NEVER)
        @DisplayName("When provided a faulty contact then should throw an exception")
        void whenProvidedAFaultyContact_thenShouldThrowAnException() {
            final Contact newContact = new Contact("Aunt Julia");
            newContact.putPhoneNumber("home", "+1(61)7982-7401");
            newContact.putPhoneNumber("new", "juliaandherself.1@gmail.com");
            final Address homeAddress = Address.builder()
                .country("US")
                .street("456 Roosevelt Street")
                .city("San Francisco")
                .zipcode("94124")
                .build();
            newContact.putAddress("home", homeAddress);

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.saveWithUser(newContact, Users.JOE.id()));

            assertThat(throwable).isNotNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "9i450gs", "auth0|c7b8835b2947d4bcc799dca5"})
        @Transactional(propagation = Propagation.NEVER)
        @DisplayName("When saving a contact with a non-existing user then should throw an exception")
        void whenSavingAContactWithANonExistingUser_thenShouldThrowAnException(String userId) {
            final Contact newContact = new Contact("boss");
            newContact.putPhoneNumber("office", "+1(57)8131-9975");
            newContact.putEmail("business", "main.contact@company.com");
            final Address companyAddress = Address.builder()
                .country("US")
                .street("1767 Heavner Avenue")
                .state("Georgia")
                .city("Carrollton")
                .zipcode("30117")
                .build();
            newContact.putAddress("work", companyAddress);

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.saveWithUser(newContact, userId));

            assertThat(throwable).isNotNull();
        }
    }

    @Nested
    public class UpdateWithUser {

        @Test
        @DisplayName("Should update a contact for a user successfully")
        void shouldUpdateAContactForAUserSuccessfully() {
            final String contactNewName = "Alex";
            final ContactData contactData =  ContactData.builder()
                .id(getContactsForJoe().get(0).getId())
                .name(contactNewName)
                .addresses(Collections.emptyMap())
                .emails(Collections.emptyMap())
                .phoneNumbers(Collections.emptyMap())
                .build();

            final Contact contact = Contact.toContact(contactData);
            contact.setUser(getMockedUser(Users.JOE.id(), "joe"));

            contactManagerServiceUnderTest.updateWithUser(contact, Users.JOE.id());

            final Contact contactFromDatabase = contactRepository.findById(contactData.getId()).orElse(null);

            assertThat(contactFromDatabase).isNotNull();
            assertThat(contact.getName()).isEqualTo(contactNewName);
        }

        @Test
        @DisplayName("When provided a non-existing contact to update then should throw an exception")
        void whenProvidedANonExistingContactToUpdate_thenShouldThrowAnException() {
            final Contact nonExistingContact = getFirstContact();
            final Throwable throwable = catchThrowable(() ->
                contactManagerServiceUnderTest.updateWithUser(nonExistingContact, Users.JOE.id())
            );

            assertResponseStatusException(throwable, "Contact not found", HttpStatus.NOT_FOUND);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "9uja48sg908","auth0|c7b8835b2947d4bcc799dca5" })
        @DisplayName("When provided an unknown userId then should throw an exception")
        void whenProvidedAnUnknownUserId_thenShouldThrowAnException(String userId) {
            final Contact contact = getContactById(UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7"));

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.updateWithUser(contact, userId));

            assertResponseStatusException(throwable, "Contact belongs to another user", HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    public class FindById {

        @Test
        @DisplayName("When provided a valid contactId then should return a contact successfully")
        void whenProvidedAValidContactId_thenShouldReturnAContactSuccessfully() {
            final UUID contactId = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");

            final Contact actualContact = contactManagerServiceUnderTest.findById(contactId);

            assertThat(actualContact).isNotNull();
            assertThat(actualContact).extracting(Contact::getName).isEqualTo("Sister Monica");
        }

        @Test
        @DisplayName("When provided a non-existing contactId then should throw an exception")
        void whenProvidedANonExistingContactId_thenShouldThrowAnException() {
            final UUID nonExistingContactId = UUID.fromString("a1e2e3ab-d8ec-47b6-b834-729dbbe3d890");

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.findById(nonExistingContactId));

            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(ResponseStatusException.class);
            assertThat(throwable).hasMessageContaining("Contact not found");
        }
    }

    @Nested
    public class DeleteByIdWithUser {

        @Test
        @DisplayName("Should delete a contact for a user successfully")
        void shouldDeleteAContactForAUserSuccessfully() {
            final UUID targetUuid = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");

            contactManagerServiceUnderTest.deleteByIdWithUser(targetUuid, Users.JOE.id());

            final List<Contact> actualContacts = contactRepository.findAll();

            assertThat(actualContacts.size()).isEqualTo(6);
            assertThat(actualContacts).filteredOn(it -> it.getUser().getUsername().equals("joe")).hasSize(2);
        }

        @Test
        @DisplayName("When provided a non-existing contactId then should throw an exception")
        void whenProvidedANonExistingContactId_thenShouldThrowAnException() {
            final UUID targetUUID = UUID.fromString("bc27c837-d7ec-4d5f-890f-c92277799fa5");

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.deleteByIdWithUser(targetUUID, Users.JOE.id()));

            assertResponseStatusException(throwable, "Contact not found", HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("When trying to delete a contact that does not belong to a user then should throw an exception")
        void whenTryingToDeleteAContactThatDoesNotBelongToAUser_thenShouldThrowAnException() {
            final UUID targetUUID = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");

            final Throwable throwable = catchThrowable(() -> contactManagerServiceUnderTest.deleteByIdWithUser(targetUUID, Users.ROBERT.id()));

            assertResponseStatusException(throwable, "Contact belongs to another user", HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    public class DeleteAllContacts {

        @Test
        @DisplayName("Should delete all contacts")
        void shouldDeleteAllContacts() {
            contactManagerServiceUnderTest.deleteAll();

            final List<Contact> listOfContacts = contactManagerServiceUnderTest.findAll();

            Assertions.assertThat(listOfContacts).hasSize(0);
        }
    }

    private void assertResponseStatusException(final Throwable throwable, final String message, final HttpStatus httpStatus) {
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat((ResponseStatusException)throwable).satisfies(rse -> {
            assertThat(rse.getReason()).isEqualTo(message);
            assertThat(rse.getStatusCode()).isEqualTo(httpStatus);
        });
    }
}