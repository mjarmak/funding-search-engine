package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.Project;

public interface ProjectMapper {

    static ProjectDTO map(Project project, boolean isSearch, boolean isFavorite) {
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .call(isSearch ? null : CallMapper.map(project.getCall(), false, false, false))
                .partners(isSearch ? null : PartnerMapper.map(project.getOrganisationProjectJoins()))
                .startDate(project.getStartDate().atStartOfDay())
                .endDate(project.getEndDate().atStartOfDay())
                .fundingOrganisation(project.getFundingOrganisationDisplayString())
                .fundingEU(project.getFundingEUDisplayString())
                .acronym(project.getAcronym())
                .status(project.getStatus())
                .signDate(isSearch ? null : project.getSignDate().atStartOfDay())
                .masterCallIdentifier(project.getMasterCallIdentifier())
                .legalBasis(project.getLegalBasis())
                .fundingScheme(project.getFundingScheme().getName())
                .longTexts(CallMapper.map(project.getLongTexts()))
                .favorite(isFavorite)
                .url(isSearch ? null : project.getUrl())
                .build();
    }

}
