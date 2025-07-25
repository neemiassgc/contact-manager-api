package contact.manager.api.contact;

import contact.manager.api.misc.Tools;
import contact.manager.api.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
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

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant addedOn = Instant.now();

    @Setter(AccessLevel.PUBLIC)
    @Column
    private LocalDate birthday;

    @Column(length = 100)
    @Setter(AccessLevel.PUBLIC)
    private String company;

    @Column(length = 100)
    @Setter(AccessLevel.PUBLIC)
    private String role;

    @ElementCollection
    @CollectionTable(name = "phone_numbers", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "mark", length = 25)
    @Column(name = "phone_number", length = 15)
    private Map<String, String> phoneNumberMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "emails", joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "mark", length = 25)
    @Column(name = "email", length = 20)
    private Map<String, String> emailMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "addresses",  joinColumns = @JoinColumn(name = "contact_id"))
    @MapKeyColumn(name = "mark", length = 25)
    private Map<String, Address> addressMap = new HashMap<>();

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter(AccessLevel.PUBLIC)
    private User user;

    public Contact(final String name, final User user) {
        this(name);
        this.user = user;
    }

    public Contact(final String name, final UUID id) {
        this(name);
        this.id = id;
    }

    public Contact(final String name) {
        this.name = name;
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

    public ContactData toContactDta() {
        return new ContactData(this);
    }

    public static Contact toContact(final ContactData contactData, UUID id) {
        final Contact newContact = toContact(contactData);
        newContact.setId(id);
        return newContact;
    }

    public static Contact toContact(final ContactData contactData) {
        final Contact newContact = new Contact(contactData.getName());
        newContact.setPhoneNumberMap(contactData.getPhoneNumbers());
        newContact.setEmailMap(contactData.getEmails());
        newContact.setAddressMap(contactData.getAddresses());
        newContact.setId(contactData.getId());
        newContact.setBirthday(contactData.getBirthday());
        newContact.setCompany(contactData.getCompany());
        newContact.setRole(contactData.getRole());
        return newContact;
    }

    public static List<ContactData> toListOfContactData(final List<Contact> contacts) {
        return contacts.stream().map(Contact::toContactDta).collect(Collectors.toList());
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