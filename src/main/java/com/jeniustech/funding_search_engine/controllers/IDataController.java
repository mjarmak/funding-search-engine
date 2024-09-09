package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IDataController<DTO> {

    ResponseEntity<DTO> getGraphMesh(@PathVariable Long id);

    ResponseEntity<DTO> getById(@PathVariable Long id,
                                @AuthenticationPrincipal Jwt jwt);
    ResponseEntity<Void> favorite(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    );
    ResponseEntity<Void> unFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    );
    ResponseEntity<SearchDTO<DTO>> getFavorites(
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Jwt jwt
    );
    ResponseEntity<List<String>> getSearchHistory(
            @AuthenticationPrincipal Jwt jwt
    );
}
