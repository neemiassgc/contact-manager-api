package com.spring.boot.entities.projections;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.embeddables.Address;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public final class ContactSummary {

    private final String name;
    private final Map<String, String> phoneNumberMap;
    private final Map<String, String> emailMap;
    private final Map<String, Address> addressMap;

    public ContactSummary(final Contact contact) {
        this.name = contact.getName();
        this.phoneNumberMap = contact.getPhoneNumberMap();
        this.emailMap = contact.getEmailMap();
        this.addressMap = contact.getAddressMap();
    }
}