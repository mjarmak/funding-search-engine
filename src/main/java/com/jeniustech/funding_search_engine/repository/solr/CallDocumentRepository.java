package com.jeniustech.funding_search_engine.repository.solr;

import com.jeniustech.funding_search_engine.entities.CallDocument;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallDocumentRepository extends SolrCrudRepository<CallDocument, Long> {

}
