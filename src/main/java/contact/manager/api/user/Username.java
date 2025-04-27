package contact.manager.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public final class Username {

    @Size(min = 3, max = 20, message = "username needs to be between 3 and 20 characters long")
    @Pattern(regexp = "[a-zA-z0-9.@]{3,}", message = "username must not have special characters")
    @NotNull(message = "username is required")
    @JsonProperty("username")
    private String value;
}