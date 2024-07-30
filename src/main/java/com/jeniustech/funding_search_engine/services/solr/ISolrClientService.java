package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.exceptions.DocumentSaveException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.models.JwtModel;
import jakarta.validation.constraints.NotNull;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;

public interface ISolrClientService<T> {
    default UpdateResponse add(SolrInputDocument document, int duration) {
        return add(List.of(document), duration);
    };
    UpdateResponse add(List<SolrInputDocument> document, int duration) throws DocumentSaveException;
    SearchDTO<T> search(String query, int pageNumber, int pageSize, @NotNull List<StatusFilterEnum> statusFilters, JwtModel jwtModel) throws SearchException;
}
