package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.models.JwtModel;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public interface ISolrClientService<T> {
    UpdateResponse add(SolrInputDocument document, int duration) throws DocumentSaveException;

    SearchDTO<T> search(String query, int pageNumber, int pageSize, JwtModel jwtModel) throws SearchException;
}
