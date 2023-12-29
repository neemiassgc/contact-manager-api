package com.spring.boot.entities;

import com.spring.boot.entities.embeddables.Address;
import com.spring.boot.entities.projections.ContactSummary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "contacts")
@NoArgsConstructor
public class Contact {

    @Id
    @UuidGenerator
    @Column(name = "contact_id")
    private UUID id;

    @Column(nullable = false, length = 140)
    @Setter
    private String name;

    @ElementCollection
    @CollectionTable(name = "phone_numbers", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "type", length = 15)
    @Column(name = "phone_number", length = 20)
    private final Map<String, String> phoneNumberMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "emails", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "type", length = 15)
    @Column(name = "email", length = 20)
    private final Map<String, String> emailMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "addresses",  joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "type", length = 15)
    private final Map<String, Address> addressMap = new HashMap<>();

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private User user;

    public Contact(final String name, final User user) {
        this(name);
        this.user = user;
    }

    public Contact(final String name) {
        this.name = name;
    }

    public Map<String, String> getPhoneNumberMap() {
        return Collections.unmodifiableMap(phoneNumberMap);
    }

    public Map<String, Address> getAddressMap() {
        return Collections.unmodifiableMap(addressMap);
    }

    public Map<String, String> getEmailMap() {
        return Collections.unmodifiableMap(emailMap);
    }

    public void putPhoneNumber(final String type, final String phoneNumber) {
        phoneNumberMap.put(type, phoneNumber);
    }

    public void putEmail(final String type, final String email) {
        emailMap.put(type, email);
    }

    public void putAddress(final String type, final Address address) {
        addressMap.put(type, Objects.requireNonNull(address));
    }

    public ContactSummary toContactSummary() {
        return new ContactSummary(this);
    }

    public static Contact toContact(final ContactSummary contactSummary, final User user) {
        final Contact newContact = new Contact();
        newContact.setUser(user);
        newContact.setName(newContact.getName());
        contactSummary.getPhoneNumberMap().forEach(newContact::putPhoneNumber);
        contactSummary.getEmailMap().forEach(newContact::putEmail);
        contactSummary.getAddressMap().forEach(newContact::putAddress);
        return newContact;
    }

    public static List<ContactSummary> toListOfContactSummary(final List<Contact> contacts) {
        return contacts.stream().map(Contact::toContactSummary).collect(Collectors.toList());
    }
}