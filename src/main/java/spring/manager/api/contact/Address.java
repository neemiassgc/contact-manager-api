package spring.manager.api.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import spring.manager.api.contact.constraint.Max;
import spring.manager.api.contact.constraint.Min;

import java.util.Objects;

@Embeddable
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotNull(message = "Country is missing")
    @Size
    @Min(value = 3, message = "Country is too short")
    @Max(value = 20, message = "Country is too long")
    @Column(nullable = false, length = 20)
    private String country;

    @NotNull(message = "Street is missing")
    @Min(value = 4, message = "Street is too short")
    @Max(value = 50, message = "Street is too long")
    @Column(nullable = false, length = 50)
    private String street;

    @NotNull(message = "City is missing")
    @Min(value = 3, message = "City is too short")
    @Max(value = 50, message = "City is too long")
    @Column(nullable = false, length = 50)
    private String city;

    @NotNull(message = "State is missing")
    @Min(value = 3, message = "State is too short")
    @Max(value = 20, message = "State is too long")
    @Column(nullable = false, length = 20)
    private String state;

    @NotNull(message = "Zipcode is missing")
    @Min(value = 5, message = "Zipcode is too short")
    @Max(value = 15, message = "Zipcode is too long")
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