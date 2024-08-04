package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.entities.UserProjectJoin;

import java.util.List;

public interface ProjectMapper {

    static List<ProjectDTO> mapJoin(List<UserProjectJoin> project, boolean isSearch, boolean isFavorite) {
        if (project == null) {
            return null;
        }
        return project.stream().map(p -> mapJoin(p, isSearch, isFavorite)).toList();
    }
    static ProjectDTO mapJoin(UserProjectJoin project, boolean isSearch, boolean isFavorite) {
        if (project == null) {
            return null;
        }
        return map(project.getProjectData(), isSearch, isFavorite, project.getProjectData().getFundingOrganisationDisplayString(), project.getProjectData().getFundingEUDisplayString());
    }
    static List<ProjectDTO> map(List<Project> projects, boolean isSearch, boolean isFavorite) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(project -> map(project, isSearch, isFavorite, project.getFundingOrganisationDisplayString(), project.getFundingEUDisplayString())).toList();
    }
    static ProjectDTO map(Project project, boolean isSearch, boolean isFavorite, String fundingOrganisationDisplayString, String fundingEUDisplayString) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .call(isSearch ? null : CallMapper.map(project.getCall(), false, false, false))
                .startDate(project.getStartDate().atStartOfDay())
                .endDate(project.getEndDate().atStartOfDay())
                .fundingOrganisation(fundingOrganisationDisplayString)
                .fundingEU(fundingEUDisplayString)
                .acronym(project.getAcronym())
                .status(isSearch ? null : project.getStatus())
                .signDate(isSearch ? null : project.getSignDate().atStartOfDay())
                .masterCallIdentifier(isSearch ? null : project.getMasterCallIdentifier())
                .legalBasis(isSearch ? null : project.getLegalBasis())
                .fundingScheme(isSearch ? null : project.getFundingScheme().getName())
                .longTexts(isSearch ? null : CallMapper.map(project.getLongTexts()))
                .favorite(isFavorite)
                .url(isSearch ? null : project.getUrl())
                .build();
    }

    static List<PartnerDTO> map(List<OrganisationProjectJoin> organisationProjectJoins, boolean isSearch) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(organisationProjectJoin -> PartnerMapper.map(organisationProjectJoin, null, null,
                        organisationProjectJoin.getFundingOrganisationDisplayString(),
                        organisationProjectJoin.getFundingEUDisplayString(),
                        null, isSearch, false))
                .toList();
    }
}
