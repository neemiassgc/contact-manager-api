package spring.manager.api.contact;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import spring.manager.api.misc.Tools;
import spring.manager.api.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@Table(name = "contacts")
@NoArgsConstructor
public class Contact {

    @Id
    @UuidGenerator
    @Column(name = "contact_id")
    private UUID id;

    @Column(nullable = false, length = 140)
    private String name;

    @ElementCollection
    @CollectionTable(name = "phone_numbers", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "label", length = 15)
    @Column(name = "phone_number", length = 15)
    private Map<String, String> phoneNumberMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "emails", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "label", length = 15)
    @Column(name = "email", length = 20)
    private Map<String, String> emailMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "addresses",  joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "label", length = 15)
    private Map<String, Address> addressMap = new HashMap<>();

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter(AccessLevel.PUBLIC)
    private User user;

    public Contact(final String name, final User user) {
        this(name);
        this.user = user;
    }

    public Contact(final String name) {
        this.name = name;
    }

    public Contact(final String name, final UUID id) {
        this.name = name;
        this.id = id;
    }

    public Map<String, String> getPhoneNumberMap() {
        return Tools.immutableMap(phoneNumberMap);
    }

    public Map<String, Address> getAddressMap() {
        return Tools.immutableMap(addressMap);
    }

    public Map<String, String> getEmailMap() {
        return Tools.immutableMap(emailMap);
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
        final Contact newContact = toContact(contactSummary);
        newContact.setUser(user);
        return newContact;
    }

    public static Contact toContact(final ContactSummary contactSummary, final UUID id) {
        final Contact newContact = toContact(contactSummary);
        newContact.setId(id);
        return newContact;
    }

    public static Contact toContact(final ContactSummary contactSummary) {
        final Contact newContact = new Contact(contactSummary.getName());
        newContact.setPhoneNumberMap(contactSummary.getPhoneNumbers());
        newContact.setEmailMap(contactSummary.getEmails());
        newContact.setAddressMap(contactSummary.getAddresses());
        return newContact;
    }

    public static List<ContactSummary> toListOfContactSummary(final List<Contact> contacts) {
        return contacts.stream().map(Contact::toContactSummary).collect(Collectors.toList());
    }

    public void merge(final Contact contactToMerge) {
        if (Objects.nonNull(contactToMerge.getName()) && !contactToMerge.getName().isBlank()) setName(contactToMerge.getName());
        if (Objects.nonNull(contactToMerge.getPhoneNumberMap()) && !contactToMerge.getPhoneNumberMap().isEmpty())
            setPhoneNumberMap(contactToMerge.getPhoneNumberMap());
        if (Objects.nonNull(contactToMerge.getEmailMap())) setEmailMap(contactToMerge.getEmailMap());
        if (Objects.nonNull(contactToMerge.getAddressMap())) setAddressMap(contactToMerge.getAddressMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phoneNumberMap, emailMap, addressMap, user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof final Contact thatContact)) return false;
        return
            Objects.equals(name, thatContact.getName()) &&
            Objects.equals(id, thatContact.getId()) &&
            Objects.equals(phoneNumberMap, thatContact.getPhoneNumberMap()) &&
            Objects.equals(emailMap, thatContact.getEmailMap()) &&
            Objects.equals(addressMap, thatContact.getAddressMap());
    }
}