package spring.manager.api.contact;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class ConstrainedContact {

    @Min(value = 2, message = "name is too short")
    @Max(value = 140, message = "name is too long")
    private final String name;

    @Size(min = 1, max = 20, message = "phoneNumbers must have between 1 and 20 items")
    private final Map<
        @Max(value = 15, message = "label is too long")
        @Min(value = 3, message = "label is too short") String,
        @Pattern(regexp = "^\\+[1-9]\\d+$", message = "phone number must be just numbers")
        @Min(value = 10, message = "phone number is too short")
        @Max(value = 15, message = "phone number is too long") String> phoneNumbers;

    @Size(max = 20, message = "emails must have a maximum of 20 items")
    private final Map<
        @Max(value = 15, message = "label is too long")
        @Min(value = 3, message = "label is too short") String,
        @Email(message = "email must be a well-formed email address") String> emails;

    @Size(max = 20, message = "addresses must have a maximum of 20 items")
    private final Map<
        @Max(value = 15, message = "label is too long")
        @Min(value = 3, message = "label is too short") String, @Valid Address> addresses;
}