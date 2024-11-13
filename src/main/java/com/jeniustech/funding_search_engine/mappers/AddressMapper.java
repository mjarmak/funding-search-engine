package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.AddressDTO;
import com.jeniustech.funding_search_engine.dto.LocationCoordinatesDTO;
import com.jeniustech.funding_search_engine.entities.Address;
import com.jeniustech.funding_search_engine.entities.LocationCoordinates;

public interface AddressMapper {

    static AddressDTO map(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDTO.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .postCode(address.getPostCode())
                .country(address.getCountry())
                .countryName(address.getCountry() == null ? null : address.getCountry().getDisplayName())
                .build();
    }

    static LocationCoordinatesDTO map(LocationCoordinates locationCoordinates) {
        if (locationCoordinates == null) {
            return null;
        }
        return LocationCoordinatesDTO.builder()
                .x(locationCoordinates.getX())
                .y(locationCoordinates.getY())
                .build();
    }

}
