package com.spring.boot;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.User;
import com.spring.boot.entities.embeddables.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TestResources {

    private final static List<Contact> contacts = new ArrayList<>();

    static {
        final User thomas = new User("thomas");
        final Contact contact1 = new Contact("Craig Bennett", UUID.randomUUID());
        contact1.setUser(thomas);
        contact1.putPhoneNumber("home", "+44  078 5357 6955");
        contact1.putEmail("main", "ethan.morris@yahoo.co.uk");
        contact1.putAddress("home", Address.builder()
            .street("2062 Maud Street")
            .city("Dollarbeg")
            .state("Delaware")
            .country("United Kindom")
            .zipcode("FK14 9HE").build()
        );

        final Contact contact2 = new Contact("Maisie Harris", thomas);
        contact2.putPhoneNumber("office", "+44  077 8877 6574");
        contact2.putEmail("main", "saunders.danielle@allen.info");
        contact2.putAddress("home", Address.builder()
            .country("United Kindom")
            .city("Star")
            .state("Delaware")
            .street("2062 Maud Street")
            .zipcode("KY7 7EY").build()
        );

        final Contact contact3 = new Contact("Lauren Bailey", thomas);
        contact3.putPhoneNumber("home", "+44 078 8085 6986");
        contact3.putEmail("main", "yrichards@hotmail.co.uk");
        contact3.putAddress("home", Address.builder()
            .country("United Kindom")
            .street("2062 Maud Street")
            .city("Bont Newydd")
            .state("Delaware")
            .zipcode("LL40 2WX").build()
        );

        contacts.add(contact1);
        contacts.add(contact2);
        contacts.add(contact3);
    }

    public static Contact getFirstContact() {
        return contacts.get(0);
    }

    public static List<Contact> getAFewContacts(final int count) {
        return contacts.subList(0, count > contacts.size() ? contacts.size() - 1 : count);
    }

    public static Contact makeCopy(final Contact contacToBeCopied) {
        final Contact newContact = new Contact(contacToBeCopied.getName(), contacToBeCopied.getId());
        newContact.setUser(contacToBeCopied.getUser());
        for (final Map.Entry<String, String> entry : contacToBeCopied.getPhoneNumberMap().entrySet())
            newContact.putPhoneNumber(entry.getKey(), entry.getValue());
        for (final Map.Entry<String, String> entry : contacToBeCopied.getEmailMap().entrySet())
            newContact.putEmail(entry.getKey(), entry.getValue());
        for (final Map.Entry<String, Address> entry : contacToBeCopied.getAddressMap().entrySet()) {
            final Address address = Address.builder()
                .state(entry.getValue().getState())
                .street(entry.getValue().getStreet())
                .city(entry.getValue().getCity())
                .country(entry.getValue().getCountry())
                .zipcode(entry.getValue().getZipcode())
                .build();
            newContact.putAddress(entry.getKey(), address);
        }
        return newContact;
    }
}
