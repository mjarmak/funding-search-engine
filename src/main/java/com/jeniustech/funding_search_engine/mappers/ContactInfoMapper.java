package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.ContactInfoDTO;
import com.jeniustech.funding_search_engine.entities.OrganisationContactInfo;

import java.util.List;
import java.util.stream.Collectors;

public class ContactInfoMapper {

    static ContactInfoDTO mapToDTO(OrganisationContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        return ContactInfoDTO.builder()
                .type(contactInfo.getType())
                .name(contactInfo.getName())
                .value(contactInfo.getValue())
                .build();
    }

    static List<ContactInfoDTO> mapToDTO(List<OrganisationContactInfo> contactInfos) {
        if (contactInfos == null || contactInfos.isEmpty()) {
            return null;
        }
        return contactInfos.stream()
                .map(ContactInfoMapper::mapToDTO)
                .collect(Collectors.toList());
    }

}
