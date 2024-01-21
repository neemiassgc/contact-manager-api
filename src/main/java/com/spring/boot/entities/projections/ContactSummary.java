package com.spring.boot.entities.projections;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.embeddables.Address;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public final class ContactSummary {

    private final UUID id;
    private final String name;
    private final Map<String, String> phoneNumbers;
    private final Map<String, String> emails;
    private final Map<String, Address> addresses;

    public ContactSummary(final Contact contact) {
        this.id = contact.getId();
        this.name = contact.getName();
        this.phoneNumbers = contact.getPhoneNumberMap();
        this.emails = contact.getEmailMap();
        this.addresses = contact.getAddressMap();
    }
}