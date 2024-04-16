package spring.manager.api.contact;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public final class ContactSummary {

    private final UUID id;

    @NotNull(message = "'name' must not be null")
    @Size(min = 2, max = 140, message = "'name' must have between 2 and 140 characters")
    private final String name;

    @NotNull(message = "'phoneNumbers' must not be null")
    @NotEmpty(message = "'phoneNumbers' must have at least 1 item")
    @Size(min = 1, max = 50, message = "'phoneNumbers' must have between 1 and 50 items")
    private final Map<String, String> phoneNumbers;

    @Size(max = 50, message = "'email' must have a maximum of 50 items")
    private final Map<String, String> emails;

    @Size(max = 50, message = "'email' must have a maximum of 50 items")
    private final Map<String, Address> addresses;

    public ContactSummary(final Contact contact) {
        this.id = contact.getId();
        this.name = contact.getName();
        this.phoneNumbers = contact.getPhoneNumberMap();
        this.emails = contact.getEmailMap();
        this.addresses = contact.getAddressMap();
    }
}