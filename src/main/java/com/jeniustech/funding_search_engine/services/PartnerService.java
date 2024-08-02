package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.*;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.UserCallJoinTypeEnum;
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
    public PartnerDTO getDTOById(Long id) {
        return PartnerMapper.map(getById(id), false);
    }

    private Organisation getById(Long id) {
        return organisationRepository.findById(id).orElseThrow(() -> new CallNotFoundException("Call not found"));
    }

    @Override
    public boolean isFavorite(Long id, Long userId) {
        return userPartnerJoinRepository.findByReferenceIdAndUserIdAndType(id, userId, UserCallJoinTypeEnum.FAVORITE).isPresent();
    }

    @Override
    public void favorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);

        ValidatorService.validateUserFavorite(userData.getMainActiveSubscription(), userPartnerJoinRepository.countByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE));

        Organisation organisation = getById(id);
        if (isFavorite(organisation.getId(), userData.getId())) {
            return;
        }
        UserPartnerJoin userCallJoin = UserPartnerJoin.builder()
                .userData(userData)
                .partnerData(organisation)
                .type(UserCallJoinTypeEnum.FAVORITE)
                .build();
        userPartnerJoinRepository.save(userCallJoin);
    }

    @Override
    public void unFavorite(Long id, String subjectId) {
        UserData userData = getUserOrNotFound(subjectId);
        Organisation organisation = getById(id);
        Optional<UserPartnerJoin> userPartnerJoin = userPartnerJoinRepository.findByReferenceIdAndUserIdAndType(organisation.getId(), userData.getId(), UserCallJoinTypeEnum.FAVORITE);
        if (userPartnerJoin.isEmpty()) {
            return;
        }
        userPartnerJoinRepository.delete(userPartnerJoin.get());
    }

    @Override
    public SearchDTO<PartnerDTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize) {
        UserData userData = getUserOrNotFound(subjectId);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.sort(UserCallJoin.class).by(UserCallJoin::getId).descending());

        List<Long> ids = userPartnerJoinRepository.findByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE, pageable);
        List<PartnerDTO> results = organisationRepository.findAllById(ids).stream()
                .map(organisation -> PartnerMapper.map(organisation, true))
                .toList();

        return SearchDTO.<PartnerDTO>builder()
                .results(results)
                .totalResults(userPartnerJoinRepository.countByUserIdAndType(userData.getId(), UserCallJoinTypeEnum.FAVORITE))
                .build();
    }

    @Override
    public List<Long> checkFavorites(UserData userData, List<Long> ids) {
        return userPartnerJoinRepository.findByReferenceIdsAndType(userData.getId(), ids, UserCallJoinTypeEnum.FAVORITE);
    }


    public List<PartnerDTO> getSuggestedPartners(Long id, JwtModel jwtModel) {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserSearchPartners(userData);

        Call call = callRepository.findById(id).orElseThrow(() -> new CallNotFoundException("Call " + id + " not found"));
        String keywords = getKeywords(call);
        return searchItems(keywords);
    }

    public SearchDTO<PartnerDTO> search(String subjectId, String query) {
        UserData userData = getUserOrNotFound(subjectId);
        ValidatorService.validateUserSearch(userData);

        if (userData.getMainActiveSubscription().isTrial()) {
            throw new SubscriptionPlanException("Trial users cannot search for partners");
        }
        logService.addLog(userData, LogTypeEnum.SEARCH_PARTNER, query);
        List<PartnerDTO> results = searchItems(query);

        setFavorites(userData, results);

        return SearchDTO.<PartnerDTO>builder()
                .results(results)
                .totalResults((long) results.size())
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

    public List<PartnerDTO> searchItems(String keywords) {
        SearchDTO<ProjectDTO> projectDTOSearchDTO = projectSolrClientService.simpleSearch(keywords, 0, 10);

        List<ProjectDTO> projectDTOS = projectDTOSearchDTO.getResults();
        List<Long> projectIds = projectDTOS.stream().map(ProjectDTO::getId).toList();
        List<OrganisationProjectJoin> organisationProjectJoins = organisationProjectJoinRepository.findAllByProjectIds(projectIds);

        List<PartnerDTO> partners = new ArrayList<>();
        for (OrganisationProjectJoin join : organisationProjectJoins) {
            Long projectId = join.getProject().getId();
            ProjectDTO projectDTO = projectDTOS.stream().filter(p -> p.getId().equals(projectId)).findFirst().orElseThrow();
            Optional<PartnerDTO> partner = partners.stream().filter(p -> p.getId().equals(join.getOrganisation().getId())).findFirst();
            if (partner.isEmpty()) {
                partners.add(PartnerMapper.map(join.getOrganisation(), 1, projectDTO.getScore().intValue(), null, null, null, true));
            } else {
                partner.get().setProjectsMatched(partner.get().getProjectsMatched() + 1);
                partner.get().setMaxScore((int) (partner.get().getMaxScore() + projectDTO.getScore()));
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
}
