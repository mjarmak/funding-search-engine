package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.*;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import com.jeniustech.funding_search_engine.enums.UserJoinTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.NLPException;
import com.jeniustech.funding_search_engine.exceptions.SubscriptionPlanException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.PartnerMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.*;
import com.jeniustech.funding_search_engine.services.solr.ProjectSolrClientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PartnerService extends IDataService<PartnerDTO> {

    private final ProjectSolrClientService projectSolrClientService;
    private final CallRepository callRepository;
    private final OrganisationProjectJoinRepository organisationProjectJoinRepository;
    private final OrganisationRepository organisationRepository;
    private final NLPService nlpService;
    private final UserPartnerJoinRepository userPartnerJoinRepository;
    private final LogService logService;

    public PartnerDTO getGraphMesh(Long id) {
        return PartnerMapper.mapToGraphMesh(getById(id));
    }

    public PartnerService(
            UserDataRepository userDataRepository,
            ProjectSolrClientService projectSolrClientService,
            CallRepository callRepository,
            OrganisationProjectJoinRepository organisationProjectJoinRepository,
            OrganisationRepository organisationRepository,
            NLPService nlpService,
            UserPartnerJoinRepository userPartnerJoinRepository,
            LogService logService) {
        super(userDataRepository);
        this.projectSolrClientService = projectSolrClientService;
        this.callRepository = callRepository;
        this.organisationProjectJoinRepository = organisationProjectJoinRepository;
        this.organisationRepository = organisationRepository;
        this.nlpService = nlpService;
        this.userPartnerJoinRepository = userPartnerJoinRepository;
        this.logService = logService;
    }


    @Override
    public PartnerDTO getDTOById(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        return PartnerMapper.mapToDetails(getById(id), false, isFavorite(id, userData.getId()));
    }

    private Organisation getById(Long id) {
        return organisationRepository.findById(id).orElseThrow(() -> new CallNotFoundException("Partner not found"));
    }

    @Override
    public boolean isFavorite(Long id, Long userId) {
        return userPartnerJoinRepository.findByReferenceIdAndUserIdAndType(id, userId, UserJoinTypeEnum.FAVORITE).isPresent();
    }

    @Override
    public void favorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userPartnerJoinRepository.countByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE));

        Organisation organisation = getById(id);
        if (isFavorite(organisation.getId(), userData.getId())) {
            return;
        }
        UserPartnerJoin userCallJoin = UserPartnerJoin.builder()
                .userData(userData)
                .partnerData(organisation)
                .type(UserJoinTypeEnum.FAVORITE)
                .build();
        userPartnerJoinRepository.save(userCallJoin);
    }

    @Override
    public void unFavorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        Organisation organisation = getById(id);
        Optional<UserPartnerJoin> userPartnerJoin = userPartnerJoinRepository.findByReferenceIdAndUserIdAndType(organisation.getId(), userData.getId(), UserJoinTypeEnum.FAVORITE);
        if (userPartnerJoin.isEmpty()) {
            return;
        }
        userPartnerJoinRepository.delete(userPartnerJoin.get());
    }

    @Override
    public SearchDTO<PartnerDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserPartnerJoin.class).by(UserPartnerJoin::getId).descending());

        List<UserPartnerJoin> joins = userPartnerJoinRepository.findByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE, pageable);
        List<PartnerDTO> results = PartnerMapper.mapForFavorites(joins, true, true);

        return SearchDTO.<PartnerDTO>builder()
                .results(results)
                .totalResults(userPartnerJoinRepository.countByUserIdAndType(userData.getId(), UserJoinTypeEnum.FAVORITE))
                .build();
    }

    @Override
    public List<Long> checkFavorites(UserData userData, List<Long> ids) {
        return userPartnerJoinRepository.findByReferenceIdsAndType(userData.getId(), ids, UserJoinTypeEnum.FAVORITE);
    }


    public List<PartnerDTO> getSuggestedPartners(Long callId, JwtModel jwtModel) {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserSearchPartners(userData);

        Call call = callRepository.findById(callId).orElseThrow(() -> new CallNotFoundException("Call " + callId + " not found"));
        String keywords = getKeywords(call);
        return searchItems(keywords, null);
    }

    public SearchDTO<PartnerDTO> searchByTopic(String subjectId, String query, List<OrganisationTypeEnum> entityTypeFilters) {
        UserData userData = getUserOrNotFound(subjectId);
        ValidatorService.validateUserSearch(userData);

        if (userData.getMainActiveSubscription().isTrial()) {
            throw new SubscriptionPlanException("Trial users cannot search for partners");
        }
        logService.addLog(userData, LogTypeEnum.SEARCH_PARTNER, query);
        List<PartnerDTO> results = searchItems(query, entityTypeFilters);

        setFavorites(userData, results);

        return SearchDTO.<PartnerDTO>builder()
                .results(results)
                .totalResults((long) results.size())
                .build();
    }

    public SearchDTO<PartnerDTO> searchByName(String subjectId, String query, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);
        ValidatorService.validateUserSearch(userData);

        if (userData.getMainActiveSubscription().isTrial()) {
            throw new SubscriptionPlanException("Trial users cannot search for partners");
        }
        logService.addLog(userData, LogTypeEnum.SEARCH_PARTNER, query);

        List<PartnerDTO> results = PartnerMapper.mapToDetails(
                organisationRepository.search(
                        query.toLowerCase(),
                        PageRequest.of(pageNumber, pageSize)
                ), true, false);

        setFavorites(userData, results);

        long technicalTotalResults = organisationRepository.countSearch(query.toLowerCase());
        long totalResults = technicalTotalResults;

        if (userData.getMainActiveSubscription().isTrial()) {
            totalResults = Math.min(technicalTotalResults, 5);
        }

        return SearchDTO.<PartnerDTO>builder()
                .results(results)
                .totalResults(totalResults)
                .technicalTotalResults(technicalTotalResults)
                .build();
    }

    private void setFavorites(UserData userData, List<PartnerDTO> results) {
        List<Long> ids = results.stream().map(PartnerDTO::getId).toList();
        List<Long> favoriteIds = checkFavorites(userData, ids);
        for (PartnerDTO partnerDTO : results) {
            if (favoriteIds.contains(partnerDTO.getId())) {
                partnerDTO.setFavorite(true);
            }
        }
    }

    public List<PartnerDTO> searchItems(String keywords, List<OrganisationTypeEnum> entityTypeFilters) {
        SearchDTO<ProjectDTO> projectDTOSearchDTO = projectSolrClientService.simpleSearch(keywords, 0, 10);

        List<ProjectDTO> projectDTOS = projectDTOSearchDTO.getResults();
        List<Long> projectIds = projectDTOS.stream().map(ProjectDTO::getId).toList();
        List<OrganisationProjectJoin> organisationProjectJoins = organisationProjectJoinRepository.findAllByProjectIds(projectIds);

        List<PartnerDTO> partners = new ArrayList<>();
        for (OrganisationProjectJoin join : organisationProjectJoins) {
            Long projectId = join.getProject().getId();
            Float projectScore = projectDTOS.stream().filter(p -> p.getId().equals(projectId)).findFirst().orElseThrow().getScore();
            Optional<PartnerDTO> partner = partners.stream().filter(p -> p.getId().equals(join.getOrganisation().getId())).findFirst();
            if (partner.isEmpty()) {
                if (entityTypeFilters == null || entityTypeFilters.contains(join.getOrganisation().getType())) {
                    partners.add(
                            PartnerMapper.map(
                                    join.getOrganisation(),
                                    1,
                                    projectScore,
                                    null,
                                    null,
                                    null,
                                    true,
                                    false
                            ));
                }
            } else {
                partner.get().setProjectsMatched(partner.get().getProjectsMatched() + 1);
                partner.get().setMaxScore(partner.get().getMaxScore() + projectScore);
            }
        }
        partners.sort((p1, p2) -> Float.compare(p2.getMaxScore(), p1.getMaxScore()));
        return partners;
    }

    private String getKeywords(Call call) {
        if (call.getKeywords() != null) {
            return call.getKeywords();
        }
        String text = call.getIdentifier() + " " + call.getTitle() + " " + call.getLongTextsToString();

        try {
            String keywords = String.join(" ", nlpService.getKeywords(text));
            call.setKeywords(keywords);
            callRepository.save(call);
            return keywords;
        } catch (IOException e) {
            throw new NLPException(e.getMessage());
        }
    }

    public List<ProjectDTO> getProjectsByPartnerId(Long id) {
        return PartnerMapper.mapForPartnerDetails(organisationProjectJoinRepository.findAllByPartnerId(id), true);
    }
}
