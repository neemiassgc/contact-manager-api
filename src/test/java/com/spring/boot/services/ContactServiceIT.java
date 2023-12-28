package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.embeddables.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ContactServiceIT {

    @Autowired
    private ContactService contactService;

    @Test
    void should_return_all_the_contacts_for_a_given_user() {
        final UUID robertUserId = UUID.fromString("773d20b6-bbf1-4c10-b743-5e7b693ef3ee");
        final List<Contact> listOfContacts = contactService.findAllByUserId(robertUserId);

        assertThat(listOfContacts).hasSize(4);
        assertThat(listOfContacts).extracting(Contact::getName)
            .containsExactly("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff");
    }

    @Test
    void should_get_a_contact_and_all_its_details_successfully() {
        final UUID gregFromAccountingContactId = UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167");
        final Contact contact = contactService.fetchById(gregFromAccountingContactId);

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
        final UUID userUUID = UUID.randomUUID();
        final List<Contact> listOfContacts = contactService.findAllByUserId(userUUID);

        assertThat(listOfContacts).isEmpty();
    }

    @Test
    void should_save_a_contact_successfully() {

    }
}
