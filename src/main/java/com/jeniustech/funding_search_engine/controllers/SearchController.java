package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.services.SolrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.jeniustech.funding_search_engine.mappers.SolrMapper.SUBMISSION_DEADLINE_DATE;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SearchController {

    private final SolrClientService solrClientService;

    @GetMapping("/search")
    public SearchDTO<CallDTO> search(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = SUBMISSION_DEADLINE_DATE) String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        return solrClientService.search(
                query,
                pageNumber,
                pageSize,
                sort,
                direction
        );
    }

}
