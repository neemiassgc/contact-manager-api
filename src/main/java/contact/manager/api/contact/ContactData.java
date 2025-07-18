package contact.manager.api.contact;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public final class ContactData extends ConstrainedContact {

    private final UUID id;
    private final Instant addedOn;

    @Builder
    public ContactData(
        final UUID id,
        final String name,
        final Instant addedOn,
        final LocalDate birthday,
        final Map<String, String> phoneNumbers,
        final Map<String, String> emails,
        final Map<String, Address> addresses
    ) {
        super(name, birthday, phoneNumbers, emails, addresses);
        this.addedOn = addedOn;
        this.id = id;
    }

    public ContactData(final Contact contact) {
        super(
            contact.getName(),
            contact.getBirthday(),
            contact.getPhoneNumberMap(),
            contact.getEmailMap(),
            contact.getAddressMap()
        );
        this.id = contact.getId();
        this.addedOn = contact.getAddedOn();
    }

    @Override
    @NotNull(message = "phoneNumbers must not be missing")
    public Map<String, String> getPhoneNumbers() {
        return super.getPhoneNumbers();
    }

    @Override
    @NotNull(message = "name must not be missing")
    public String getName() {
        return super.getName();
    }
}