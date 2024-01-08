package com.spring.boot;

import com.spring.boot.entities.Contact;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class BasicUnitTests {

    @Test
    void two_contacts_should_be_equal() {
        final Contact contact1 = TestResources.getFirstContact();
        final Contact contact2 = TestResources.makeCopy(TestResources.getFirstContact());

        assertThat(contact1.equals(contact2)).isTrue();
    }
}
