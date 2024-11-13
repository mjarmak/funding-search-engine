package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.CountryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String postCode;
    private String city;
    private CountryEnum country;

    public String toString() {
        String addressString = "";
        if (street != null) {
            addressString += street + ", ";
        }
        if (postCode != null) {
            addressString += postCode + " ";
        }
        if (city != null) {
            addressString += city + ", ";
        }
        if (country != null) {
            addressString += country.getDisplayName();
        }
        return addressString;
    }

}
