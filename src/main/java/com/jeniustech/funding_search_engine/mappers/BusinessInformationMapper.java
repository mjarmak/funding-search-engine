package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.BusinessInformationDTO;
import com.jeniustech.funding_search_engine.entities.BusinessInformation;

public interface BusinessInformationMapper {

    static BusinessInformationDTO map(BusinessInformation businessInformation) {
        if (businessInformation == null) {
            return null;
        }
        return BusinessInformationDTO.builder()
            .name(businessInformation.getName())
            .phoneNumber(businessInformation.getPhoneNumber())
            .email(businessInformation.getEmail())
            .vatNumber(businessInformation.getVatNumber())
            .address(AddressMapper.map(businessInformation.getAddress()))
            .build();
    }

}
