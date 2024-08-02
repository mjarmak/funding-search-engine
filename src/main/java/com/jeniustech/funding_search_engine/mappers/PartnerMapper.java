package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public interface PartnerMapper {

    static PartnerDTO map(Organisation organisation, boolean isSearch) {
        if (organisation == null) {
            return null;
        }
        PartnerDTO partnerDTO = map(organisation, null, null, null, null, null, isSearch);
        if (!isSearch) {
            partnerDTO.setProjects(mapToProjectsDTO(organisation.getOrganisationProjectJoins()));
            // sum of all projects funding
            partnerDTO.setFundingEU(NumberMapper.shortenNumber(organisation.getOrganisationProjectJoins().stream()
                    .map(OrganisationProjectJoin::getFundingEU)
                    .reduce(BigDecimal.ZERO, BigDecimal::add), 1));
            partnerDTO.setFundingOrganisation(NumberMapper.shortenNumber(organisation.getOrganisationProjectJoins().stream()
                    .map(OrganisationProjectJoin::getFundingOrganisation)
                    .reduce(BigDecimal.ZERO, BigDecimal::add), 1));
        }
        return partnerDTO;
    }
    static List<ProjectDTO> mapToProjectsDTO(List<OrganisationProjectJoin> organisationProjectJoins) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .sorted(Comparator.comparingInt(o -> o.getType().getHierarchy()))
                .map(PartnerMapper::mapToProjectsDTO
                )
                .toList();
    }
    static ProjectDTO mapToProjectsDTO(OrganisationProjectJoin organisationProjectJoin) {
        if (organisationProjectJoin == null) {
            return null;
        }
        Project project = organisationProjectJoin.getProject();
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .startDate(project.getStartDate().atStartOfDay())
                .endDate(project.getEndDate().atStartOfDay())
                .fundingOrganisation(project.getFundingOrganisationDisplayString())
                .fundingEU(project.getFundingEUDisplayString())
                .acronym(project.getAcronym())
                .status(project.getStatus())
                .joinType(organisationProjectJoin.getType())
                .build();
    }

    static List<PartnerDTO> mapToPartnersDTO(List<OrganisationProjectJoin> organisationProjectJoins) {
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
