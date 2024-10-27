package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.entities.UserPartnerJoin;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;

import java.util.Comparator;
import java.util.List;

public interface PartnerMapper {


    static List<PartnerDTO> mapForFavorites(List<UserPartnerJoin> organisation, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return organisation.stream().map(c -> mapForFavorites(c, isSearch, isFavorite)).toList();
    }
    static PartnerDTO mapForFavorites(UserPartnerJoin organisation, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return mapToDetails(organisation.getPartnerData(), isSearch, isFavorite);
    }
    static List<PartnerDTO> mapToDetails(List<Organisation> organisations, boolean isSearch, boolean isFavorite) {
        if (organisations == null) {
            return null;
        }
        return organisations.stream().map(c -> mapToDetails(c, isSearch, isFavorite)).toList();
    }
    static PartnerDTO mapToDetails(Organisation organisation, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return map(organisation, null, null, null, null, null, isSearch, isFavorite);
    }

    static PartnerDTO map(OrganisationProjectJoin organisationProjectJoin, boolean isSearch, boolean isFavorite) {
        if (organisationProjectJoin == null) {
            return null;
        }
        return map(organisationProjectJoin.getOrganisation(), null, null, organisationProjectJoin.getFundingOrganisationDisplayString(), organisationProjectJoin.getFundingEUDisplayString(), organisationProjectJoin.getType(), isSearch, isFavorite);
    }
    static PartnerDTO map(Organisation organisation, Integer projectsMatched, Float score, String fundingOrganisation, String fundingEU, OrganisationProjectJoinTypeEnum joinType, boolean isSearch, boolean isFavorite) {
        if (organisation == null) {
            return null;
        }
        return PartnerDTO.builder()
                .id(organisation.getId())
                .name(organisation.getName())
                .shortName(organisation.getShortName())
                .address(isSearch ? null : AddressMapper.mapToDTO(organisation.getAddress()))
                .typeName(organisation.getType() != null ? organisation.getType().getDisplayName() : null)
                .sme(isSearch ? null : organisation.isSme())
                .vatNumber(isSearch ? null : organisation.getVatNumber())
                .nutsCode(isSearch ? null : organisation.getNutsCode())
                .locationCoordinates(isSearch ? null : AddressMapper.mapToDTO(organisation.getLocationCoordinates()))
                .contactInfos(isSearch ? null : ContactInfoMapper.mapToDTO(organisation.getContactInfos()))
                .projectsMatched(projectsMatched)
                .maxScore(score)
                .fundingOrganisation(fundingOrganisation)
                .fundingEU(fundingEU)
                .totalFundingEU(organisation.getFundingEUDisplayString())
                .totalFundingOrganisation(organisation.getFundingOrganisationDisplayString())
                .projectNumber(organisation.getProjectNumber())
                .joinType(joinType)
                .favorite(isFavorite)
                .build();
    }


    static List<ProjectDTO> mapForPartnerDetails(List<OrganisationProjectJoin> organisationProjectJoins, boolean isSearch) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(organisationProjectJoin -> ProjectMapper.map(
                        organisationProjectJoin.getProject(), isSearch, false,
                        organisationProjectJoin.getFundingOrganisationDisplayString(),
                        organisationProjectJoin.getFundingEUDisplayString(),
                        organisationProjectJoin.getType()))
                .sorted(Comparator.comparing(ProjectDTO::getStartDate, Comparator.nullsFirst(Comparator.naturalOrder())
                ).reversed())
                .toList();
    }

    static PartnerDTO mapToGraphMesh(Organisation partner) {
        PartnerDTO partnerDTO = mapToGraphMeshChild(partner);
        partnerDTO.setProjects(PartnerMapper.mapToProjectGraphMeshChild(partner.getOrganisationProjectJoins()));
        return partnerDTO;
    }

    static PartnerDTO mapToGraphMeshChild(Organisation partner) {
        if (partner == null) {
            return null;
        }
        return PartnerDTO.builder()
                .id(partner.getId())
                .name(partner.getName())
                .shortName(partner.getShortName())
                .totalFundingOrganisation(partner.getFundingOrganisationDisplayString())
                .totalFundingEU(partner.getFundingEUDisplayString())
                .build();
    }

    static List<ProjectDTO> mapToProjectGraphMeshChild(List<OrganisationProjectJoin> organisationProjectJoins) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(PartnerMapper::mapToProjectGraphMeshChild)
                .toList();
    }

    static ProjectDTO mapToProjectGraphMeshChild(OrganisationProjectJoin organisationProjectJoin) {
        if (organisationProjectJoin == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(organisationProjectJoin.getProject().getId())
                .title(organisationProjectJoin.getProject().getTitle())
                .acronym(organisationProjectJoin.getProject().getAcronym())
                .startDate(organisationProjectJoin.getProject().getStartDate() == null ? null : organisationProjectJoin.getProject().getStartDate().atStartOfDay())
                .endDate(organisationProjectJoin.getProject().getEndDate() == null ? null : organisationProjectJoin.getProject().getEndDate().atStartOfDay())
                .fundingOrganisation(organisationProjectJoin.getFundingOrganisationDisplayString())
                .fundingEU(organisationProjectJoin.getFundingEUDisplayString())
                .status(organisationProjectJoin.getProject().getStatus())
                .build();
    }

    static List<PartnerDTO> mapToGraphMeshChild(List<OrganisationProjectJoin> organisationProjectJoins) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(organisationProjectJoin -> PartnerMapper.mapToGraphMeshChild(
                        organisationProjectJoin.getOrganisation(),
                        organisationProjectJoin.getType(),
                        organisationProjectJoin.getFundingOrganisationDisplayString(),
                        organisationProjectJoin.getFundingEUDisplayString()))
                .toList();
    }

    static PartnerDTO mapToGraphMeshChild(Organisation partner, OrganisationProjectJoinTypeEnum type, String fundingOrganisationDisplayString, String fundingEUDisplayString) {
        if (partner == null) {
            return null;
        }
        return PartnerDTO.builder()
                .id(partner.getId())
                .name(partner.getName())
                .shortName(partner.getShortName())
                .fundingOrganisation(fundingOrganisationDisplayString)
                .fundingEU(fundingEUDisplayString)
                .joinType(type)
                .build();
    }

    static void sortByName(List<Organisation> partners) {
        if (partners == null) {
            return;
        }
        partners.sort(Comparator.comparing(Organisation::getName, Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
