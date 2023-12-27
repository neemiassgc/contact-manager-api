package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
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
    void should_save_a_contact_successfully() {

    }
}
