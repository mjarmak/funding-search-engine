package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.entities.UserProjectJoin;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;

import java.util.Comparator;
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
        return map(project.getProjectData(), isSearch, isFavorite, project.getProjectData().getFundingOrganisationDisplayString(), project.getProjectData().getFundingEUDisplayString(), null);
    }
    static List<ProjectDTO> map(List<Project> projects, boolean isSearch, boolean isFavorite) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(project -> map(project, isSearch, isFavorite, project.getFundingOrganisationDisplayString(), project.getFundingEUDisplayString(), null)).toList();
    }
    static ProjectDTO map(Project project, boolean isSearch, boolean isFavorite, String fundingOrganisationDisplayString, String fundingEUDisplayString, OrganisationProjectJoinTypeEnum joinType) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .call(isSearch ? null : CallMapper.map(project.getCall(), false, false, false))
                .startDate(project.getStartDate() == null ? null : project.getStartDate().atStartOfDay())
                .endDate(project.getEndDate() == null ? null : project.getEndDate().atStartOfDay())
                .fundingOrganisation(fundingOrganisationDisplayString)
                .fundingEU(fundingEUDisplayString)
                .totalFundingEU(project.getFundingEUDisplayString())
                .totalFundingOrganisation(project.getFundingOrganisationDisplayString())
                .acronym(project.getAcronym())
                .status(isSearch ? null : project.getStatus())
                .frameworkProgram(project.getFrameworkProgram() == null ? null : project.getFrameworkProgram().getName())
                .signDate(isSearch ? null : project.getSignDate() == null ? null : project.getSignDate().atStartOfDay())
                .callIdentifier(isSearch ? null : project.getCallIdentifier())
                .masterCallIdentifier(isSearch ? null : project.getMasterCallIdentifier())
                .legalBasis(isSearch ? null : project.getLegalBasis())
                .fundingScheme(isSearch ? null : project.getFundingSchemeName())
                .longTexts(isSearch ? null : CallMapper.map(project.getLongTexts()))
                .favorite(isFavorite)
                .url(isSearch ? null : project.getUrl())
                .joinType(joinType)
                .build();
    }

    static List<PartnerDTO> map(List<OrganisationProjectJoin> organisationProjectJoins, boolean isSearch) {
        if (organisationProjectJoins == null) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(organisationProjectJoin -> PartnerMapper.map(organisationProjectJoin, isSearch, false))
                .sorted(Comparator.comparingInt(o -> o.getJoinType().getHierarchy()))
                .toList();
    }

    static ProjectDTO mapToGraphMesh(Project project) {
        ProjectDTO projectDTO = mapToGraphMeshChild(project);
        projectDTO.setPartners(PartnerMapper.mapToGraphMeshChild(project.getOrganisationProjectJoins()));
        return projectDTO;
    }

    static ProjectDTO mapToGraphMeshChild(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .totalFundingOrganisation(project.getFundingOrganisationDisplayString())
                .totalFundingEU(project.getFundingEUDisplayString())
                .acronym(project.getAcronym())
                .status(project.getStatus())
                .build();
    }
    static List<ProjectDTO> mapToGraphMeshChild(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream().map(ProjectMapper::mapToGraphMeshChild).toList();
    }
}
