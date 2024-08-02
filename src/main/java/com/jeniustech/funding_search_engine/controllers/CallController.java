package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.CallService;
import com.jeniustech.funding_search_engine.services.PartnerService;
import com.jeniustech.funding_search_engine.services.UserDataService;
import com.jeniustech.funding_search_engine.services.solr.CallSolrClientService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/call")
@RequiredArgsConstructor
public class CallController implements IDataController<CallDTO> {

    private final CallService callService;
    private final PartnerService partnerService;
    private final UserDataService userDataService;
    private final CallSolrClientService callSolrClientService;

    @GetMapping("/search")
    public ResponseEntity<SearchDTO<CallDTO>> search(
            @RequestParam @Size(min = 2, max = 255) String query,
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @RequestParam(required = false, name = "status", defaultValue = "UPCOMING,OPEN,CLOSED"
            ) List<StatusFilterEnum> statusFilters,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(callSolrClientService.search(
                query,
                pageNumber,
                pageSize,
                statusFilters,
                jwtModel
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CallDTO> getById(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(callService.getDTOById(id, jwtModel.getUserId()));
    }

    @GetMapping("/{id}/favorite")
    public ResponseEntity<Void> favorite(@PathVariable Long id,
                                         @AuthenticationPrincipal Jwt jwt
                             ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        callService.favorite(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> unFavorite(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt
                               ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        callService.unFavorite(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<SearchDTO<CallDTO>> getFavorites(
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(callService.getFavoritesByUserId(jwtModel.getUserId(), pageNumber, pageSize));
    }

    @GetMapping("/search/history")
    public ResponseEntity<List<String>> getSearchHistory(
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.getSearchHistory(jwtModel.getUserId(), LogTypeEnum.SEARCH_CALL));
    }

    @GetMapping("/{id}/partners/recommended")
    public ResponseEntity<List<PartnerDTO>> getSuggestedPartners(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(partnerService.getSuggestedPartners(id, jwtModel));
    }

}
