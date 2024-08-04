package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserProjectJoin;
import com.jeniustech.funding_search_engine.enums.UserJoinTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.ProjectNotFoundException;
import com.jeniustech.funding_search_engine.mappers.ProjectMapper;
import com.jeniustech.funding_search_engine.repository.OrganisationProjectJoinRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.repository.UserProjectJoinRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService extends IDataService<ProjectDTO> {

    private final ProjectRepository projectRepository;
    private final UserProjectJoinRepository userProjectJoinRepository;
    private final OrganisationProjectJoinRepository organisationProjectJoinRepository;

    public ProjectService(UserDataRepository userDataRepository, ProjectRepository projectRepository, UserProjectJoinRepository userProjectJoinRepository, OrganisationProjectJoinRepository organisationProjectJoinRepository) {
        super(userDataRepository);
        this.projectRepository = projectRepository;
        this.userProjectJoinRepository = userProjectJoinRepository;
        this.organisationProjectJoinRepository = organisationProjectJoinRepository;
    }

    public ProjectDTO getDTOById(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        return ProjectMapper.map(getById(id), false, isFavorite(id, userData.getId()), getById(id).getFundingOrganisationDisplayString(), getById(id).getFundingEUDisplayString());
    }

    private Project getById(Long callId) {
        return projectRepository.findById(callId).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    public boolean isFavorite(Long id, Long userId) {
        return userProjectJoinRepository.findByReferenceIdAndUserIdAndType(id, userId, UserJoinTypeEnum.FAVORITE).isPresent();
    }

    public void favorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userProjectJoinRepository.countByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE));

        Project project = getById(id);
        if (isFavorite(project.getId(), userData.getId())) {
            return;
        }
        UserProjectJoin userProjectJoin = UserProjectJoin.builder()
                .userData(userData)
                .projectData(project)
                .type(UserJoinTypeEnum.FAVORITE)
                .build();
        userProjectJoinRepository.save(userProjectJoin);
    }

    public void unFavorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        Project project = getById(id);
        Optional<UserProjectJoin> userCallJoin = userProjectJoinRepository.findByReferenceIdAndUserIdAndType(project.getId(), userData.getId(), UserJoinTypeEnum.FAVORITE);
        if (userCallJoin.isEmpty()) {
            return;
        }
        userProjectJoinRepository.delete(userCallJoin.get());
    }

    public SearchDTO<ProjectDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserProjectJoin.class).by(UserProjectJoin::getId).descending());

        List<UserProjectJoin> joins = userProjectJoinRepository.findByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE, pageable);
        List<ProjectDTO> results = ProjectMapper.mapJoin(joins, true, true);

        return SearchDTO.<ProjectDTO>builder()
                .results(results)
                .totalResults(userProjectJoinRepository.countByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE))
                .build();
    }

    public List<Long> checkFavorites(UserData userData, List<Long> ids) {
        return userProjectJoinRepository.findByReferenceIdsAndType(userData.getId(), ids, UserJoinTypeEnum.FAVORITE);
    }

    public List<PartnerDTO> getPartnersByProjectId(Long id) {
        return ProjectMapper.map(organisationProjectJoinRepository.findAllByProjectId(id), true);
    }

    public List<ProjectDTO> getProjectsByCallId(Long id) {
        return ProjectMapper.map(projectRepository.findAllByCallId(id), true, false);
    }
}
