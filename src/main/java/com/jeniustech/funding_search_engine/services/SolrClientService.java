package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

import static com.jeniustech.funding_search_engine.constants.Constants.START_DATE;
import static com.jeniustech.funding_search_engine.constants.Constants.END_DATE;

@Service
public class SolrClientService {

    SolrClient solrClient;

    public SolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core}") String core
            ) {
        this.solrClient = new Http2SolrClient
                .Builder(url + "/" + core)
                .withConnectionTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public UpdateResponse add(SolrInputDocument document, int duration) throws DocumentSaveException {
        try {
            final UpdateResponse updateResponse =  this.solrClient.add(document, duration);
            this.solrClient.commit();
            return updateResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentSaveException("Failed to save document", e);
        }
    }

    public SearchDTO<CallDTO> search(String query, int pageNumber, int pageSize) {
        try {
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.Q, query,
                    CommonParams.START, String.valueOf(pageNumber * pageSize),
                    CommonParams.ROWS, String.valueOf(pageSize)
            );
            solrQuery.setSort("score", SolrQuery.ORDER.desc);
            // open
            solrQuery.setSort("if(ms(NOW,"+ START_DATE +") > 0, 1, 0)", SolrQuery.ORDER.asc);
            // upcoming
            solrQuery.setSort("sum(abs(ms(NOW,"+ START_DATE + ")),abs(ms(NOW," + END_DATE + ")))", SolrQuery.ORDER.asc);

            QueryResponse response = this.solrClient.query(solrQuery);
            return SearchDTO.<CallDTO>builder()
                    .results(SolrMapper.map(response.getResults()))
                    .totalResults(response.getResults().getNumFound())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SearchException("Failed to search", e);
        }
    }

}
