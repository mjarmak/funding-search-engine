package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.services.CallService;
import com.jeniustech.funding_search_engine.services.LogService;
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
public class CallSolrClientService implements ISolrClientService<CallDTO> {

    SolrClient solrClient;

    private final UserDataRepository userDataRepository;
    private final LogService logService;
    private final CallService callService;

    public CallSolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core.calls}") String core,
            UserDataRepository userDataRepository, LogService logService, CallService callService) {
        this.userDataRepository = userDataRepository;
        this.logService = logService;
        this.callService = callService;
        this.solrClient = new Http2SolrClient
                .Builder(url + "/" + core)
                .withConnectionTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public UpdateResponse add(SolrInputDocument document, int duration) throws DocumentSaveException {
        try {
            final UpdateResponse updateResponse = this.solrClient.add(document, duration);
            this.solrClient.commit();
            return updateResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentSaveException("Failed to save document", e);
        }
    }

    public SearchDTO<CallDTO> search(String query, int pageNumber, int pageSize, JwtModel jwtModel) throws SearchException {
        return search(query, pageNumber, pageSize, List.of(StatusFilterEnum.UPCOMING, StatusFilterEnum.OPEN, StatusFilterEnum.CLOSED), jwtModel);
    }

    public SearchDTO<CallDTO> search(String query, int pageNumber, int pageSize, @NotNull List<StatusFilterEnum> statusFilters, JwtModel jwtModel) throws SearchException {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserSearch(userData);

        if (userData.getMainActiveSubscription().isTrial()) {
            pageNumber = 0;
            pageSize = 5;
        }

        logService.addLog(userData, LogTypeEnum.SEARCH, query);
        try {
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.Q, query,
                    CommonParams.START, String.valueOf(pageNumber * pageSize),
                    CommonParams.ROWS, String.valueOf(pageSize)
            );
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

            QueryResponse response = this.solrClient.query(solrQuery);
            List<CallDTO> results = SolrMapper.mapToCall(response.getResults());
            List<Long> callIds = results.stream().map(CallDTO::getId).toList();

            List<Long> favoriteIds = callService.checkFavoriteCalls(userData, callIds);

            for (CallDTO callDTO : results) {
                if (favoriteIds.contains(callDTO.getId())) {
                    callDTO.setFavorite(true);
                }
            }

            long technicalTotalResults = response.getResults().getNumFound();
            long totalResults = technicalTotalResults;

            if (userData.getMainActiveSubscription().isTrial()) {
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

}
