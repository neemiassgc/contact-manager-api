package com.spring.boot.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ContactServiceIT {

    @Autowired
    private ContactService contactService;


    @Test
    void should_save_a_contact_successfully() {

    }
}
