package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.Project;

import java.util.List;

public interface ProjectMapper {

    static List<ProjectDTO> map(List<Project> projects, boolean isSearch, boolean isFavorite) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(project -> map(project, isSearch, isFavorite)).toList();
    }
    static ProjectDTO map(Project project, boolean isSearch, boolean isFavorite) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .call(isSearch ? null : CallMapper.map(project.getCall(), false, false, false))
                .partners(isSearch ? null : PartnerMapper.mapToPartnersDTO(project.getOrganisationProjectJoins()))
                .startDate(project.getStartDate().atStartOfDay())
                .endDate(project.getEndDate().atStartOfDay())
                .fundingOrganisation(project.getFundingOrganisationDisplayString())
                .fundingEU(project.getFundingEUDisplayString())
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

}
