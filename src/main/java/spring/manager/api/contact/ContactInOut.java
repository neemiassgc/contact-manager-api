package spring.manager.api.contact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public final class ContactInOut extends ConstrainedContact {

    private final UUID id;

    @Builder
    public ContactInOut(
        final UUID id,
        final String name,
        final Map<String, String> phoneNumbers,
        final Map<String, String> emails,
        final Map<String, Address> addresses
    ) {
        super(name, phoneNumbers, emails, addresses);
        this.id = id;
    }

    public ContactInOut(final UUID id, final String name, final ConstrainedContact constrainedContact) {
        this(id, name, constrainedContact.getPhoneNumbers(), constrainedContact.getEmails(), constrainedContact.getAddresses());
    }

    public ContactInOut(final ConstrainedContact constrainedContact) {
        this(null, null, constrainedContact);
    }

    public ContactInOut(final Contact contact) {
        super(contact.getName(), contact.getPhoneNumberMap(), contact.getEmailMap(), contact.getAddressMap());
        this.id = contact.getId();
    }

    @Override
    @NotNull(message = "phoneNumbers must not be missing")
    public Map<String, String> getPhoneNumbers() {
        return super.getPhoneNumbers();
    }
}