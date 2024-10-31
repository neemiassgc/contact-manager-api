package contact.manager.api.contact;

import contact.manager.api.contact.constraint.Max;
import contact.manager.api.contact.constraint.Min;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Embeddable
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotNull(message = "country is missing")
    @Size
    @Min(value = 3, message = "country is too short")
    @Max(value = 20, message = "country is too long")
    @Column(nullable = false, length = 20)
    private String country;

    @NotNull(message = "street is missing")
    @Min(value = 4, message = "street is too short")
    @Max(value = 50, message = "street is too long")
    @Column(nullable = false, length = 50)
    private String street;

    @NotNull(message = "city is missing")
    @Min(value = 3, message = "city is too short")
    @Max(value = 50, message = "city is too long")
    @Column(nullable = false, length = 50)
    private String city;

    @NotNull(message = "state is missing")
    @Min(value = 3, message = "state is too short")
    @Max(value = 20, message = "state is too long")
    @Column(nullable = false, length = 20)
    private String state;

    @NotNull(message = "zipcode is missing")
    @Min(value = 5, message = "zipcode is too short")
    @Max(value = 15, message = "zipcode is too long")
    @Column(nullable = false, length = 15)
    private String zipcode;

    @Override
    public int hashCode() {
        return Objects.hash(country, state, street, city, zipcode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof final Address thatAddress)) return false;
        return
            Objects.equals(country, thatAddress.getCountry()) &&
            Objects.equals(street, thatAddress.getStreet()) &&
            Objects.equals(city, thatAddress.getCity()) &&
            Objects.equals(state, thatAddress.getState()) &&
            Objects.equals(zipcode, thatAddress.getZipcode());
    }
}