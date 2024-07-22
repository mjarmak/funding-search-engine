package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.SavedSearchDTO;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.SavedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SavedSearchController {

    private final SavedSearchService savedSearchService;

    @PostMapping("/search/saved")
    public ResponseEntity<SavedSearchDTO> savedSearch(
            @RequestBody SavedSearchDTO savedSearchDTO,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(savedSearchService.saveSearch(
                jwtModel.getUserId(),
                savedSearchDTO
        ));
    }

    @PutMapping("/search/saved/{id}")
    public ResponseEntity<SavedSearchDTO> updateSavedSearch(
            @PathVariable Long id,
            @RequestBody SavedSearchDTO savedSearchDTO,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(savedSearchService.updateSearch(
                id,
                jwtModel.getUserId(),
                savedSearchDTO
        ));
    }

    @GetMapping("/search/saved")
    public ResponseEntity<List<SavedSearchDTO>> getUserSavedSearches(
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(savedSearchService.getUserSavedSearches(jwtModel.getUserId()));
    }

    @DeleteMapping("/search/saved/{id}")
    public ResponseEntity<Void> deleteSavedSearch(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        savedSearchService.deleteSearch(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

}
