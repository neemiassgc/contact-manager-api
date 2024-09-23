package spring.manager.api.contact;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import spring.manager.api.contact.constraint.Max;
import spring.manager.api.contact.constraint.Min;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class ContactEntries {

    @Size(min = 1, max = 20, message = "phoneNumbers must have between 1 and 50 items")
    private final Map<String,
        @Pattern(regexp = "^\\+[1-9]\\d+$", message = "Phone number must be just numbers")
        @Min(value = 10, message = "Phone number is too short")
        @Max(value = 15, message = "Phone number is too long") String> phoneNumbers;

    @Size(max = 20, message = "emails must have a maximum of 20 items")
    private final Map<String, @Email String> emails;

    @Size(max = 20, message = "addresses must have a maximum of 20 items")
    private final Map<String, @Valid Address> addresses;
}