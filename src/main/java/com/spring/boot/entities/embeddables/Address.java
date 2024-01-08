package com.spring.boot.entities.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.Objects;

@Embeddable
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(nullable = false, length = 20)
    private String country;

    @Column(nullable = false, length = 50)
    private String street;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(nullable = false, length = 20)
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