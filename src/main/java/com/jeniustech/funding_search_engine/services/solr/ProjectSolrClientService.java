package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
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

@Service
public class ProjectSolrClientService implements ISolrClientService<ProjectDTO> {

    SolrClient solrClient;

    public ProjectSolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core.projects}") String core
    ) {
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

    public SearchDTO<ProjectDTO> search(String query, int pageNumber, int pageSize, JwtModel jwtModel) throws SearchException {
        try {
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.Q, query,
                    CommonParams.START, String.valueOf(pageNumber * pageSize),
                    CommonParams.ROWS, String.valueOf(pageSize)
            );
            solrQuery.addField("*");
            solrQuery.addField("score");
            solrQuery.setSort("score", SolrQuery.ORDER.desc);

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

}
