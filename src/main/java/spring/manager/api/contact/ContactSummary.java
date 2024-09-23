package spring.manager.api.contact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.manager.api.contact.constraint.Max;
import spring.manager.api.contact.constraint.Min;

import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public final class ContactSummary extends ContactEntries {

    private final UUID id;

    @NotNull(message = "name must not be missing")
    @Min(value = 2, message = "name is too short")
    @Max(value = 140, message = "name is too long")
    private final String name;

    @Builder
    public ContactSummary(
        final UUID id,
        final String name,
        final Map<String, String> phoneNumbers,
        final Map<String, String> emails,
        final Map<String, Address> addresses
    ) {
        super(phoneNumbers, emails, addresses);
        this.id = id;
        this.name = name;
    }

    public ContactSummary(final UUID id, final String name, final ContactEntries contactEntries) {
        this(id, name, contactEntries.getPhoneNumbers(), contactEntries.getEmails(), contactEntries.getAddresses());
    }

    public ContactSummary(final ContactEntries contactEntries) {
        this(null, null, contactEntries);
    }

    public ContactSummary(final Contact contact) {
        super(contact.getPhoneNumberMap(), contact.getEmailMap(), contact.getAddressMap());
        this.id = contact.getId();
        this.name = contact.getName();
    }

    @Override
    @NotNull(message = "phoneNumbers must not be missing")
    public Map<String, String> getPhoneNumbers() {
        return super.getPhoneNumbers();
    }
}