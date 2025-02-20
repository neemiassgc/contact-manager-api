package contact.manager.api.contact;

import contact.manager.api.contact.constraint.Min;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class ConstrainedContact {

    @contact.manager.api.contact.constraint.Min(value = 2, message = "name is too short")
    @contact.manager.api.contact.constraint.Max(value = 140, message = "name is too long")
    private final String name;

    @Size(min = 1, max = 20, message = "phoneNumbers must have between 1 and 20 items")
    private final Map<
        @contact.manager.api.contact.constraint.Max(value = 15, message = "mark is too long")
        @contact.manager.api.contact.constraint.Min(value = 3, message = "mark is too short") String,
        @Pattern(regexp = "^\\+[1-9]\\d+$", message = "phone number must be just numbers")
        @contact.manager.api.contact.constraint.Min(value = 10, message = "phone number is too short")
        @contact.manager.api.contact.constraint.Max(value = 15, message = "phone number is too long") String> phoneNumbers;

    @Size(max = 20, message = "emails must have a maximum of 20 items")
    private final Map<
        @contact.manager.api.contact.constraint.Max(value = 15, message = "mark is too long")
        @contact.manager.api.contact.constraint.Min(value = 3, message = "mark is too short") String,
        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be a well-formed email address") String> emails;

    @Size(max = 20, message = "addresses must have a maximum of 20 items")
    private final Map<
        @contact.manager.api.contact.constraint.Max(value = 15, message = "mark is too long")
        @Min(value = 3, message = "mark is too short") String, @Valid Address> addresses;
}