package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.exceptions.SubscriptionPlanException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.services.LogService;
import com.jeniustech.funding_search_engine.services.PartnerService;
import com.jeniustech.funding_search_engine.services.ValidatorService;
import com.jeniustech.funding_search_engine.util.StringUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jeniustech.funding_search_engine.util.StringUtil.processQuery;

@Service
public class PartnerSolrClientService implements ISolrClientService<PartnerDTO> {

    SolrClient solrClient;
    private final UserDataRepository userDataRepository;
    private final LogService logService;
    private final PartnerService partnerService;

    public PartnerSolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core.partners}") String core,
            UserDataRepository userDataRepository,
            LogService logService,
            PartnerService partnerService
    ) {
        this.userDataRepository = userDataRepository;
        this.logService = logService;
        this.partnerService = partnerService;
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

    public SearchDTO<PartnerDTO> search(String query, int pageNumber, int pageSize, JwtModel jwtModel, List<OrganisationTypeEnum> entityTypeFilters) throws SearchException {
        UserData userData = this.userDataRepository.findBySubjectId(jwtModel.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserSearch(userData);
        if (userData.getMainActiveSubscription().isTrial()) {
            throw new SubscriptionPlanException("Trial users cannot search for partners");
        }

        logService.addLog(userData, LogTypeEnum.SEARCH_PARTNER, query);
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
            solrQuery.addFilterQuery("{!frange l=1}query($q)");

            if (!entityTypeFilters.isEmpty() && entityTypeFilters.size() < 5) {
                solrQuery.addFilterQuery("type:(" + entityTypeFilters.stream().map(OrganisationTypeEnum::getName).collect(Collectors.joining(" ")) + ")");
            }

            QueryResponse response = this.solrClient.query(solrQuery);
            List<PartnerDTO> results = SolrMapper.mapToPartner(response.getResults());
            List<Long> ids = results.stream().map(PartnerDTO::getId).toList();

            List<Long> favoriteIds = partnerService.checkFavorites(userData, ids);

            for (PartnerDTO partnerDTO : results) {
                if (favoriteIds.contains(partnerDTO.getId())) {
                    partnerDTO.setFavorite(true);
                }
            }

            long technicalTotalResults = response.getResults().getNumFound();
            long totalResults = technicalTotalResults;

            if (userData.getMainActiveSubscription().isTrial()) {
                totalResults = Math.min(technicalTotalResults, 5);
            }

            return SearchDTO.<PartnerDTO>builder()
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
