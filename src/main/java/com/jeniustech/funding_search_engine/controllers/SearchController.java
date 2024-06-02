package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.services.SolrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://app-funding.jarmak.tech"})
public class SearchController {

    private final SolrClientService solrClientService;

    @GetMapping("/search")
    public SearchDTO<CallDTO> search(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return solrClientService.search(
                query,
                pageNumber,
                pageSize
        );
    }

}
