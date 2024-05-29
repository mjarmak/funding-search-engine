package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.services.SolrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    private final SolrClientService solrClientService;

    @GetMapping("/search")
    @CrossOrigin(origins = "http://localhost:4200")
    public SearchDTO<CallDTO> search(@RequestParam(required = true) String query) {
        return solrClientService.search(query);
    }

}
