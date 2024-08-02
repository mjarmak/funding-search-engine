package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.PartnerService;
import com.jeniustech.funding_search_engine.services.UserDataService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController implements IDataController<PartnerDTO> {

    private final PartnerService partnerService;
    private final UserDataService userDataService;

    @GetMapping("/search")
    public ResponseEntity<SearchDTO<PartnerDTO>> search(
            @RequestParam @Size(min = 2, max = 255) String query,
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @RequestParam(required = false, name = "status", defaultValue = "UPCOMING,OPEN,CLOSED"
            ) List<StatusFilterEnum> statusFilters,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(partnerService.search(
                jwtModel.getUserId(),
                query
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(partnerService.getDTOById(id));
    }

    @GetMapping("/{id}/favorite")
    public ResponseEntity<Void> favorite(@PathVariable Long id,
                                         @AuthenticationPrincipal Jwt jwt
                             ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        partnerService.favorite(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> unFavorite(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt
                               ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        partnerService.unFavorite(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<SearchDTO<PartnerDTO>> getFavorites(
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(partnerService.getFavoritesByUserId(jwtModel.getUserId(), pageNumber, pageSize));
    }

    @GetMapping("/search/history")
    public ResponseEntity<List<String>> getSearchHistory(
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.getSearchHistory(jwtModel.getUserId(), LogTypeEnum.SEARCH_PARTNER));
    }

}
