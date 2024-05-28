package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.services.SolrClientService;
import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    private final SolrClientService solrClientService;

    @GetMapping("/search")
    public QueryResponse search(String query) {
        return solrClientService.search(query);
    }

}
