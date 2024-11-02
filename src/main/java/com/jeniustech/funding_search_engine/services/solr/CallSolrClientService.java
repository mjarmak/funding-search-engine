package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.exceptions.SubscriptionPlanException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.services.CallService;
import com.jeniustech.funding_search_engine.services.LogService;
import com.jeniustech.funding_search_engine.services.ValidatorService;
import jakarta.validation.constraints.NotNull;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CallSolrClientService implements ISolrClientService<CallDTO> {

    public static final int MIN_SCORE = 1;
    SolrClient solrClient;
    private final UserDataRepository userDataRepository;
    private final LogService logService;
    private final CallService callService;

    public CallSolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core.calls}") String core,
            UserDataRepository userDataRepository,
            LogService logService,
            CallService callService
    ) {
        this.userDataRepository = userDataRepository;
        this.logService = logService;
        this.callService = callService;
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

    public List<CallDTO> searchAfterDate(String query, LocalDateTime date) throws SearchException {
        try {
            String dateString = DateMapper.mapToSolrString(date);
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.Q, query,
                    CommonParams.FQ, "created_date:[" + dateString + " TO *]"
            );
            solrQuery.addField("*");
            solrQuery.setSort("score", SolrQuery.ORDER.desc);
            solrQuery.addFilterQuery("{!frange l=2}query($q)");

            QueryResponse response = this.solrClient.query(solrQuery);
            return SolrMapper.mapToCall(response.getResults());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SearchException("Failed to search", e);
        }
    }

    public SearchDTO<CallDTO> search(String query, int pageNumber, int pageSize, @NotNull List<StatusFilterEnum> statusFilters, JwtModel jwtModel) throws SearchException {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserSearch(userData);

        boolean isTrialUser = userData.getMainActiveSubscription().isTrial();
        if (isTrialUser) {
            pageNumber = 0;
            pageSize = 5;
            if (query.isBlank()) {
                throw new SubscriptionPlanException("Trial user can't search without a query");
            }
        }

        logService.addLog(userData, LogTypeEnum.SEARCH_CALL, query);
        try {
            QueryResponse response = search(query, pageNumber, pageSize, statusFilters, MIN_SCORE);

            var maxScore = response.getResults().getMaxScore();
            float minScoreNew = maxScore / 2;
            if (minScoreNew > MIN_SCORE && response.getResults().getNumFound() > 1000 && !query.isBlank() && !isTrialUser) {
                log.info("Max score too high, retrying with min score: {}", minScoreNew);
                response = search(query, pageNumber, pageSize, statusFilters, minScoreNew);
            }

            List<CallDTO> results = SolrMapper.mapToCall(response.getResults());
            List<Long> ids = results.stream().map(CallDTO::getId).toList();

            List<Long> favoriteIds = callService.checkFavorites(userData, ids);

            for (CallDTO callDTO : results) {
                if (favoriteIds.contains(callDTO.getId())) {
                    callDTO.setFavorite(true);
                }
            }

            long technicalTotalResults = response.getResults().getNumFound();
            long totalResults = technicalTotalResults;

            if (isTrialUser) {
                totalResults = Math.min(technicalTotalResults, 5);
            }

            return SearchDTO.<CallDTO>builder()
                    .results(results)
                    .totalResults(totalResults)
                    .technicalTotalResults(response.getResults().getNumFound())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SearchException("Failed to search", e);
        }
    }

    private QueryResponse search(String query, int pageNumber, int pageSize, List<StatusFilterEnum> statusFilters, float minScore) throws SolrServerException, IOException {
        final SolrQuery solrQuery = new SolrQuery(
                CommonParams.START, String.valueOf(pageNumber * pageSize),
                CommonParams.ROWS, String.valueOf(pageSize)
        );
        if (!query.isBlank()) {
            solrQuery.setQuery(query);
            // set def type to dismax
            solrQuery.set("defType", "dismax");
            solrQuery.addFilterQuery("{!frange l=" + minScore + "}query($q)");
        } else {
            solrQuery.setQuery("*:*");
        }
        solrQuery.addField("*");
        solrQuery.addField("score");
        solrQuery.setSort("score", SolrQuery.ORDER.desc);

        if (!statusFilters.isEmpty() && statusFilters.size() < 3) {
            List<String> filters = new ArrayList<>();
            for (StatusFilterEnum statusFilter : statusFilters) {
                switch (statusFilter) {
                    case UPCOMING -> filters.add("(start_date:[NOW TO *])");
                    case OPEN ->
                            filters.add("((start_date:[* TO NOW] AND end_date:[NOW TO *]) OR (start_date:[* TO NOW] AND end_date_2:[NOW TO *]))");
                    case CLOSED ->
                            filters.add("((end_date:[* TO NOW] AND -end_date_2:*) OR (end_date:[* TO NOW] AND end_date_2:[* TO NOW]))");
                }
            }
            // join with 'OR'
            solrQuery.addFilterQuery(String.join(" OR ", filters));
        }

        return this.solrClient.query(solrQuery);
    }
}
