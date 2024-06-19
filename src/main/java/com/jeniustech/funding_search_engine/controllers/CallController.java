package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.dto.SearchDTO;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CallController {

    private final CallService callService;

    @GetMapping("/call/{id}")
    public ResponseEntity<CallDTO> getCallById(@PathVariable Long id) {
        return ResponseEntity.ok(callService.getCallDTOById(id));
    }

    @GetMapping("/call/{id}/favorite")
    public ResponseEntity<Void> favoriteCall(@PathVariable Long id,
                             @AuthenticationPrincipal Jwt jwt
                             ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        callService.favoriteCall(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/call/{id}/favorite")
    public ResponseEntity<Void> unFavoriteCall(@PathVariable Long id,
                               @AuthenticationPrincipal Jwt jwt
                               ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        callService.unFavoriteCall(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/call/favorites")
    public ResponseEntity<SearchDTO<CallDTO>> getFavoriteCalls(
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(callService.getFavoritesByUserId(jwtModel.getUserId(), pageNumber, pageSize));
    }

}
