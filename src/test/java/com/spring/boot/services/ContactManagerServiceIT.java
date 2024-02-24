package com.spring.boot.services;

import com.spring.boot.TestResources;
import com.spring.boot.entities.Contact;
import com.spring.boot.entities.embeddables.Address;
import com.spring.boot.entities.projections.ContactSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@Transactional
public class ContactManagerServiceIT {

    @Autowired
    private ContactManagerService contactManagerService;

    @Autowired
    private UserService userService;

    @Test
    void should_return_all_of_the_contacts_available() {
        List<Contact> contactList = contactManagerService.findAll();

        assertThat(contactList).hasSize(7);
        assertThat(contactList).extracting(Contact::getPhoneNumberMap).flatMap(Map::values).hasSize(12);
        assertThat(contactList).extracting(Contact::getAddressMap).flatMap(Map::values).hasSize(11);
        assertThat(contactList).extracting(Contact::getEmailMap).flatMap(Map::values).hasSize(10);
    }

    @Test
    void should_return_all_the_contacts_for_a_given_user() {
        final List<Contact> listOfContacts = contactManagerService.findAllByUsername("robert");

        assertThat(listOfContacts).hasSize(4);
        assertThat(listOfContacts).extracting(Contact::getName)
            .containsExactly("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff");
    }

    @Test
    void should_get_a_contact_of_a_user_and_all_its_details_successfully() {
        final UUID gregFromAccountingContactId = UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167");
        final Contact contact = contactManagerService.findByIdWithUser(gregFromAccountingContactId, "joe");

        assertThat(contact).isNotNull();
        assertThat(contact).extracting(Contact::getName).isEqualTo("Greg from accounting");
        assertThat(contact).extracting(Contact::getPhoneNumberMap).satisfies(phoneNumberMap -> {
            assertThat(phoneNumberMap).isNotNull();
            assertThat(phoneNumberMap).hasSize(1);
            assertThat(phoneNumberMap).containsOnly(Map.entry("home", "+359(26)5948-0427"));
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
    void should_return_an_empty_list_of_contacts_for_a_user_that_does_not_exist() {
        final List<Contact> listOfContacts = contactManagerService.findAllByUsername("Lorena");

        assertThat(listOfContacts).isEmpty();
    }

    @Test
    void should_throw_an_error_when_a_contact_is_not_found() {
        final UUID contactUUID = UUID.randomUUID();

        final Throwable throwable = catchThrowable(() -> contactManagerService.findByIdWithUser(contactUUID, "joe"));

        assertResponseStatusException(throwable, "Contact not found", HttpStatus.NOT_FOUND);
    }

    @Test
    void should_throw_an_error_when_finding_a_contact_that_does_not_belong_to_the_user() {
        final UUID contactId = UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167");

        final Throwable throwable = catchThrowable(() -> contactManagerService.findByIdWithUser(contactId, "robert"));

        assertResponseStatusException(throwable, "Contact does not belong to the user: robert", HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_save_a_new_contact_successfully() {
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

        contactManagerService.saveWithUser(newContact, "robert");

        final List<Contact> listOfContacts = contactManagerService.findAll();
        assertThat(listOfContacts).hasSize(8);
        assertThat(listOfContacts).extracting(Contact::getPhoneNumberMap).flatMap(Map::values).hasSize(13);
        assertThat(listOfContacts).extracting(Contact::getAddressMap).flatMap(Map::values).hasSize(12);
        assertThat(listOfContacts).extracting(Contact::getEmailMap).flatMap(Map::values).hasSize(11);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void should_throw_an_error_when_creating_a_new_contact_if_any_field_is_missing_to_set() {
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

        final Throwable throwable = catchThrowable(() -> contactManagerService.saveWithUser(newContact, "joe"));

        assertThat(throwable).isNotNull();
    }

    @Test
    void should_update_a_contact_successfully() {
        final String contactNewName = "Alex";
        final ContactSummary contactSummary =   ContactSummary.builder()
            .id(UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7"))
            .name(contactNewName)
            .addresses(null)
            .emails(null)
            .phoneNumbers(null)
            .build();

        final Contact referenceContact = Contact.toContact(contactSummary, contactSummary.getId());

        contactManagerService.updateWithUser(referenceContact, "joe");

        final Contact contactFromStorage = contactManagerService.findById(referenceContact.getId());

        assertThat(referenceContact.getName()).isEqualTo(contactNewName);
    }

    @Test
    void should_throw_an_exception_when_it_is_tried_to_update_a_contact_that_does_not_exist() {
        final Contact nonExistingContact = TestResources.getFirstContact();
        final Throwable throwable = catchThrowable(() -> contactManagerService.updateWithUser(nonExistingContact, "joe"));

        assertResponseStatusException(throwable, "Contact not found", HttpStatus.NOT_FOUND);
    }

    @Test
    void should_throw_an_exception_when_trying_to_update_a_contact_that_does_not_belong_to_a_user() {
        final Contact contact = TestResources.getContactById(UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7"));

        final Throwable throwable = catchThrowable(() -> contactManagerService.updateWithUser(contact, "robert"));

        assertResponseStatusException(throwable, "Contact does not belong to the user: robert", HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_delete_a_contact_successfully() {
        final UUID targetUuid = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");
        contactManagerService.deleteByIdWithUser(targetUuid, "joe");

        final Throwable throwable = catchThrowable(() -> contactManagerService.findById(targetUuid));
        final long count = contactManagerService.findAll().size();

        assertThat(throwable).isNotNull();
        assertThat(count).isEqualTo(6);
    }

    @Test
    void should_throw_an_error_when_trying_to_delete_a_contact_that_does_not_exist() {
        final UUID targetUUID = UUID.randomUUID();

        final Throwable throwable = catchThrowable(() -> contactManagerService.deleteByIdWithUser(targetUUID, "joe"));

        assertResponseStatusException(throwable, "Contact not found", HttpStatus.NOT_FOUND);
    }

    @Test
    void should_throw_an_error_when_trying_to_delete_a_contact_that_does_not_belong_to_a_user() {
        final UUID targetUUID = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");

        final Throwable throwable = catchThrowable(() -> contactManagerService.deleteByIdWithUser(targetUUID, "robert"));

        assertResponseStatusException(throwable, "Contact does not belong to the user: robert", HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_delete_all_the_contacts() {
        contactManagerService.deleteAll();

        final List<Contact> listOfContacts = contactManagerService.findAll();

        assertThat(listOfContacts).hasSize(0);
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