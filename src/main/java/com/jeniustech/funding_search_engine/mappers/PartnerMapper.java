package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.entities.UserPartnerJoin;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;
import com.jeniustech.funding_search_engine.util.StringUtil;

import java.util.List;

public interface PartnerMapper {


    static List<PartnerDTO> mapjoin(List<UserPartnerJoin> organisation, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return organisation.stream().map(c -> mapjoin(c, isSearch, isFavorite)).toList();
    }
    static PartnerDTO mapjoin(UserPartnerJoin organisation, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return mapToDetails(organisation.getPartnerData(), isSearch, isFavorite);
    }
    static PartnerDTO mapToDetails(Organisation organisation, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return map(organisation, null, null, null, null, null, isSearch, isFavorite);
    }

    static PartnerDTO map(OrganisationProjectJoin organisationProjectJoin, Integer projectsMatched, Integer score, String fundingOrganisation, String fundingEU, OrganisationProjectJoinTypeEnum joinType, boolean isSearch, boolean isFavorite) {
        if (organisationProjectJoin == null) {
            return null;
        }
        return map(organisationProjectJoin.getOrganisation(), projectsMatched, score, organisationProjectJoin.getFundingOrganisationDisplayString(), organisationProjectJoin.getFundingEUDisplayString(), joinType, isSearch, isFavorite);
    }
    static PartnerDTO map(Organisation organisation, Integer projectsMatched, Integer score, String fundingOrganisation, String fundingEU, OrganisationProjectJoinTypeEnum joinType, boolean isSearch, boolean isFavorite) {
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
                .fundingOrganisation(StringUtil.isNotEmpty(fundingOrganisation) ? fundingOrganisation : organisation.getFundingOrganisationDisplayString())
                .fundingEU(StringUtil.isNotEmpty(fundingEU) ? fundingEU : organisation.getFundingEUDisplayString())
                .projectNumber(organisation.getProjectNumber())
                .joinType(joinType)
                .favorite(isFavorite)
                .build();
    }


    static List<ProjectDTO> map(List<OrganisationProjectJoin> organisationProjectJoins, boolean isSearch) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(organisationProjectJoin -> ProjectMapper.map(
                        organisationProjectJoin.getProject(), isSearch, false,
                        organisationProjectJoin.getFundingOrganisationDisplayString(),
                        organisationProjectJoin.getFundingEUDisplayString()))
                .toList();
    }

}
