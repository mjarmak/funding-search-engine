package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
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

@Service
public class SolrClientService {

    private final String core;

    SolrClient solrClient;

    public SolrClientService(
            @Value("${spring.data.solr.host}") String url,
            @Value("${spring.data.solr.core}") String core
            ) {
        this.core = core;
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

    public QueryResponse search(String query) {
        try {
            final SolrQuery solrQuery = new SolrQuery(
                    CommonParams.Q, query,
                    "sort", "identifier",
                    "rows", "10"
            );
            return this.solrClient.query(core, solrQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
