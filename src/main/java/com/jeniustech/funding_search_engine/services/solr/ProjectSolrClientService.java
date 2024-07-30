package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
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
import jakarta.validation.constraints.NotNull;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
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
                    CommonParams.Q, query,
                    CommonParams.START, String.valueOf(pageNumber * pageSize),
                    CommonParams.ROWS, String.valueOf(pageSize)
            );
            solrQuery.addField("*");
            solrQuery.addField("score");
            solrQuery.setSort("score", SolrQuery.ORDER.desc);
            solrQuery.add("q.op", "OR");

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

    public SearchDTO<ProjectDTO> search(String query, int pageNumber, int pageSize, @NotNull List<StatusFilterEnum> statusFilters, JwtModel jwtModel) throws SearchException {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserSearch(userData);

        if (userData.getMainActiveSubscription().isTrial()) {
            pageNumber = 0;
            pageSize = 5;
        }

        logService.addLog(userData, LogTypeEnum.SEARCH_PROJECT, query);
        try {
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.Q, query,
                    CommonParams.START, String.valueOf(pageNumber * pageSize),
                    CommonParams.ROWS, String.valueOf(pageSize)
            );
            solrQuery.addField("*");
            solrQuery.addField("score");
            solrQuery.setSort("score", SolrQuery.ORDER.desc);
            solrQuery.addFilterQuery("{!frange l=2}query($q)");

            if (!statusFilters.isEmpty() && statusFilters.size() < 3) {
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

            QueryResponse response = this.solrClient.query(solrQuery);
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

            if (userData.getMainActiveSubscription().isTrial()) {
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

}
