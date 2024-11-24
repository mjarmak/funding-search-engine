package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.FrameworkProgramEnum;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.services.LogService;
import com.jeniustech.funding_search_engine.services.ProjectService;
import com.jeniustech.funding_search_engine.services.ValidatorService;
import com.jeniustech.funding_search_engine.util.StringUtil;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jeniustech.funding_search_engine.util.QueryUtil.getMinScore;
import static com.jeniustech.funding_search_engine.util.StringUtil.processQuery;

@Service
@Slf4j
public class ProjectSolrClientService implements ISolrClientService<ProjectDTO> {

    SolrClient solrClient;
    private final UserDataRepository userDataRepository;
    private final LogService logService;
    private final ProjectService projectService;

    public ProjectSolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core.projects}") String core,
            UserDataRepository userDataRepository,
            LogService logService,
            ProjectService projectService
    ) {
        this.userDataRepository = userDataRepository;
        this.logService = logService;
        this.projectService = projectService;
        this.solrClient = new Http2SolrClient
                .Builder(url + "/" + core)
                .withConnectionTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public UpdateResponse add(List<SolrInputDocument> document, int duration) throws DocumentSaveException {
        try {
            final UpdateResponse updateResponse = this.solrClient.add(document, duration);
            this.solrClient.commit();
            return updateResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentSaveException("Failed to save document", e);
        }
    }

    public SearchDTO<ProjectDTO> simpleSearch(String query, int pageNumber, int pageSize) throws SearchException {
        try {
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.START, String.valueOf(pageNumber * pageSize),
                    CommonParams.ROWS, String.valueOf(pageSize)
            );

            solrQuery.setQuery(processQuery(query));
            String operationType = StringUtil.isQuoted(query) ? "AND" : "OR";
            solrQuery.set("q.op", operationType);

            solrQuery.addField("*");
            solrQuery.addField("score");
            solrQuery.setSort("score", SolrQuery.ORDER.desc);
            Float minScore = getMinScore(query);
            solrQuery.addFilterQuery("{!frange l=" + minScore + "}query($q)");

            QueryResponse response = this.solrClient.query(solrQuery);
            List<ProjectDTO> results = SolrMapper.mapToProject(response.getResults());

            return SearchDTO.<ProjectDTO>builder()
                    .results(results)
                    .totalResults(response.getResults().getNumFound())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SearchException("Failed to search", e);
        }
    }

    public SearchDTO<ProjectDTO> search(
            String query,
            int pageNumber,
            int pageSize,
            @Nullable List<StatusFilterEnum> statusFilters,
            @Nullable List<FrameworkProgramEnum> programFilters,
            JwtModel jwtModel
    ) throws SearchException {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserSearch(userData);

        boolean isTrialUser = userData.getMainActiveSubscription().isTrial();
        if (isTrialUser) {
            pageNumber = 0;
            pageSize = 5;
        }

        logService.addLog(userData, LogTypeEnum.SEARCH_PROJECT, query);
        try {
            QueryResponse response = search(query, pageNumber, pageSize, statusFilters, programFilters, getMinScore(query));

//            var maxScore = response.getResults().getMaxScore();
//            float minScoreNew = maxScore / 2;
//            if (minScoreNew > MIN_SCORE && response.getResults().getNumFound() > 1000 && !query.isBlank() && !isTrialUser) {
//                log.debug("Max score too high, retrying with min score: {}", minScoreNew);
//                response = search(query, pageNumber, pageSize, statusFilters, programFilters, minScoreNew);
//            }

            List<ProjectDTO> results = SolrMapper.mapToProject(response.getResults());
            List<Long> ids = results.stream().map(ProjectDTO::getId).toList();

            List<Long> favoriteIds = projectService.checkFavorites(userData, ids);

            for (ProjectDTO projectDTO : results) {
                if (favoriteIds.contains(projectDTO.getId())) {
                    projectDTO.setFavorite(true);
                }
            }

            long technicalTotalResults = response.getResults().getNumFound();
            long totalResults = technicalTotalResults;

            if (isTrialUser) {
                totalResults = Math.min(technicalTotalResults, 5);
            }


            return SearchDTO.<ProjectDTO>builder()
                    .results(results)
                    .totalResults(totalResults)
                    .technicalTotalResults(response.getResults().getNumFound())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SearchException("Failed to search", e);
        }
    }

    private QueryResponse search(String query, int pageNumber, int pageSize, @Nullable List<StatusFilterEnum> statusFilters, @Nullable List<FrameworkProgramEnum> programFilters, @Nullable Float minScore) throws SolrServerException, IOException {
        final SolrQuery solrQuery = new SolrQuery(
                CommonParams.START, String.valueOf(pageNumber * pageSize),
                CommonParams.ROWS, String.valueOf(pageSize)
        );

        solrQuery.setQuery(processQuery(query));
        String operationType = StringUtil.isQuoted(query) ? "AND" : "OR";
        solrQuery.set("q.op", operationType);

        solrQuery.addField("*");
        solrQuery.addField("score");
        solrQuery.setSort("score", SolrQuery.ORDER.desc);
        // set def type to dismax
        solrQuery.set("defType", "dismax");

        if (minScore != null) {
            solrQuery.addFilterQuery("{!frange l=" + minScore + "}query($q)");
        }

        if (statusFilters != null && !statusFilters.isEmpty() && statusFilters.size() < 3) {
            List<String> filters = new ArrayList<>();
            for (StatusFilterEnum statusFilter : statusFilters) {
                switch (statusFilter) {
                    case UPCOMING -> filters.add("(start_date:[NOW TO *])");
                    case OPEN ->
                            filters.add("(start_date:[* TO NOW] AND end_date:[NOW TO *])");
                    case CLOSED ->
                            filters.add("(end_date:[* TO NOW])");
                }
            }
            // join with 'OR'
            solrQuery.addFilterQuery(String.join(" OR ", filters));
        }

        if (programFilters != null && !programFilters.isEmpty() && programFilters.size() < 9) {
            solrQuery.addFilterQuery("framework_program:(" + programFilters.stream().map(FrameworkProgramEnum::getName).collect(Collectors.joining(" ")) + ")");
        }

        return this.solrClient.query(solrQuery);
    }

}
