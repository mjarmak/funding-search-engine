package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.SolrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SolrClientService solrClientService;

    @GetMapping("/search")
    public ResponseEntity<SearchDTO<CallDTO>> search(
            @RequestParam String query,
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(solrClientService.search(
                query,
                pageNumber,
                pageSize,
                jwtModel
        ));
    }

}
