package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.OrganisationDTO;
import com.jeniustech.funding_search_engine.entities.Organisation;

public interface OrganisationMapper {

    static OrganisationDTO mapToDTO(Organisation organisation) {
        if (organisation == null) {
            return null;
        }
        return OrganisationDTO.builder()
                .id(organisation.getId())
                .name(organisation.getName())
                .shortName(organisation.getShortName())
                .address(AddressMapper.mapToDTO(organisation.getAddress()))
                .type(organisation.getType())
                .sme(organisation.isSme())
                .vatNumber(organisation.getVatNumber())
                .nutsCode(organisation.getNutsCode())
                .locationCoordinates(AddressMapper.mapToDTO(organisation.getLocationCoordinates()))
                .contactInfos(ContactInfoMapper.mapToDTO(organisation.getContactInfos()))
                .build();
    }

}
