package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;

import java.util.Comparator;
import java.util.List;

public interface PartnerMapper {

    static PartnerDTO map(Organisation organisation, boolean isSearch) {
        if (organisation == null) {
            return null;
        }
        return map(organisation, null, null, null, null, null, isSearch);
    }
    static List<PartnerDTO> map(List<OrganisationProjectJoin> organisationProjectJoins) {
        if (organisationProjectJoins == null) {
            return null;
        }
       return organisationProjectJoins.stream()
                   .sorted(Comparator.comparingInt(o -> o.getType().getHierarchy()))
                .map(organisationProjectJoin -> map(organisationProjectJoin.getOrganisation(),
                        null,
                        null,
                        organisationProjectJoin.getFundingOrganisationDisplayString(),
                        organisationProjectJoin.getFundingEUDisplayString(),
                        organisationProjectJoin.getType(), true
                ))
               .toList();
    }
    static PartnerDTO map(Organisation organisation, Integer projectsMatched, Integer score, String fundingOrganisation, String fundingEU, OrganisationProjectJoinTypeEnum joinType, boolean isSearch) {
        if (organisation == null) {
            return null;
        }
        return PartnerDTO.builder()
                .id(organisation.getId())
                .name(organisation.getName())
                .shortName(organisation.getShortName())
                .address(isSearch ? null : AddressMapper.mapToDTO(organisation.getAddress()))
                .typeName(organisation.getType() != null ? organisation.getType().getName() : null)
                .sme(isSearch ? null : organisation.isSme())
                .vatNumber(isSearch ? null : organisation.getVatNumber())
                .nutsCode(isSearch ? null : organisation.getNutsCode())
                .locationCoordinates(isSearch ? null : AddressMapper.mapToDTO(organisation.getLocationCoordinates()))
                .contactInfos(isSearch ? null : ContactInfoMapper.mapToDTO(organisation.getContactInfos()))
                .projectsMatched(projectsMatched)
                .maxScore(score)
                .fundingOrganisation(fundingOrganisation)
                .fundingEU(fundingEU)
                .joinType(joinType)
                .build();
    }

}
