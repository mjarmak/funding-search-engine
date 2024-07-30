package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.entities.UserCallJoin;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserProjectJoin;
import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.ProjectNotFoundException;
import com.jeniustech.funding_search_engine.mappers.ProjectMapper;
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

    public ProjectService(UserDataRepository userDataRepository, ProjectRepository projectRepository, UserProjectJoinRepository userProjectJoinRepository) {
        super(userDataRepository);
        this.projectRepository = projectRepository;
        this.userProjectJoinRepository = userProjectJoinRepository;
    }

    public ProjectDTO getDTOById(Long id) {
        return ProjectMapper.map(getById(id), false, false);
    }

    private Project getById(Long callId) {
        return projectRepository.findById(callId).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    public boolean isFavorite(Long callId, Long userId) {
        return userProjectJoinRepository.findByReferenceIdAndUserIdAndType(callId, userId, UserCallJoinTypeEnum.FAVORITE).isPresent();
    }

    public void favorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userProjectJoinRepository.countByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE));

        Project project = getById(id);
        if (isFavorite(project.getId(), userData.getId())) {
            return;
        }
        UserProjectJoin userProjectJoin = UserProjectJoin.builder()
                .userData(userData)
                .projectData(project)
                .type(UserCallJoinTypeEnum.FAVORITE)
                .build();
        userProjectJoinRepository.save(userProjectJoin);
    }

    public void unFavorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        Project project = getById(id);
        Optional<UserProjectJoin> userCallJoin = userProjectJoinRepository.findByReferenceIdAndUserIdAndType(project.getId(), userData.getId(), UserCallJoinTypeEnum.FAVORITE);
        if (userCallJoin.isEmpty()) {
            return;
        }
        userProjectJoinRepository.delete(userCallJoin.get());
    }

    public SearchDTO<ProjectDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserCallJoin.class).by(UserCallJoin::getId).descending());

        List<Long> ids = userProjectJoinRepository.findByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE, pageable);
        List<ProjectDTO> results = projectRepository.findAllById(ids).stream()
                .map(project -> ProjectMapper.map(project, false, false))
                .toList();

        return SearchDTO.<ProjectDTO>builder()
                .results(results)
                .totalResults(userProjectJoinRepository.countByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE))
                .build();
    }

    public List<Long> checkFavorites(UserData userData, List<Long> ids) {
        return userProjectJoinRepository.findByReferenceIdsAndType(userData.getId(), ids, UserCallJoinTypeEnum.FAVORITE);
    }
}
