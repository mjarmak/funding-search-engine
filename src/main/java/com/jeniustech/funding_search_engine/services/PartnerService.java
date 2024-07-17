package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.NLPException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.OrganisationProjectJoinRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.services.solr.ProjectSolrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final ProjectSolrClientService projectSolrClientService;
    private final CallRepository callRepository;
    private final UserDataRepository userDataRepository;
    private final OrganisationProjectJoinRepository organisationProjectJoinRepository;
    private final NLPService nlpService;

    public List<PartnerDTO> getSuggestedPartners(Long callId, JwtModel jwtModel) {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserSearchPartners(userData);

        Call call = callRepository.findById(callId).orElseThrow(() -> new CallNotFoundException("Call " + callId + " not found"));
        String text = call.getIdentifier() + " " + call.getTitle() + " " + call.getLongTextsToString();

        List<String> keywords;
        try {
            keywords = nlpService.getKeywords(text);
        } catch (IOException e) {
            throw new NLPException(e.getMessage());
        }
        SearchDTO<ProjectDTO> projectDTOSearchDTO = projectSolrClientService.search(String.join(" ", keywords), 0, 10, null);

        List<ProjectDTO> projectDTOS = projectDTOSearchDTO.getResults();
        List<Long> projectIds = projectDTOS.stream().map(ProjectDTO::getId).toList();
        List<OrganisationProjectJoin> organisationProjectJoins = organisationProjectJoinRepository.findAllByProjectIds(projectIds);

        List<PartnerDTO> partners = new ArrayList<>();
        for (OrganisationProjectJoin join : organisationProjectJoins) {
            Long projectId = join.getProject().getId();
            ProjectDTO projectDTO = projectDTOS.stream().filter(p -> p.getId().equals(projectId)).findFirst().orElseThrow();
            Optional<PartnerDTO> partner = partners.stream().filter(p -> p.getOrganisationId().equals(join.getOrganisation().getId())).findFirst();
            if (partner.isEmpty()) {
                partners.add(
                        PartnerDTO.builder()
                                .organisationId(join.getOrganisation().getId())
                                .projectId(projectId)
                                .name(join.getOrganisation().getShortNameOrName())
                                .type(join.getOrganisation().getType())
                                .country(join.getOrganisation().getAddress().getCountry())
                                .projectsMatched(1)
                                .maxScore((int) projectDTO.getScore())
                                .build());
            } else {
                partner.get().setProjectsMatched(partner.get().getProjectsMatched() + 1);
                partner.get().setMaxScore((int) (partner.get().getMaxScore() + projectDTO.getScore()));
            }
        }
        partners.sort((p1, p2) -> Float.compare(p2.getMaxScore(), p1.getMaxScore()));
        return partners.subList(0, Math.min(partners.size(), 10));
    }

    private static String filterQuery(StringBuilder query) {
        return query.toString().replace(":", "")
                .replace("(", "")
                .replace(")", "")
                .replace("[", "")
                .replace("]", "")
                .replace("{", "")
                .replace("}", "")
                .replace("^", "")
                .replace("~", "")
                .replace("*", "")
                .replace("?", "")
                .replace("!", "");
    }

}
