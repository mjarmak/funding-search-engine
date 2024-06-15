package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.UserDataDTO;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserDataController {

    private final UserDataService userDataService;

    @GetMapping("/data")
    public ResponseEntity<UserDataDTO> getUserData(
            @AuthenticationPrincipal Jwt jwt
            ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.getUserDataOrCreate(jwtModel));
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<UserDataDTO>> getUsersBySubscriptionId(
            @PathVariable Long subscriptionId,
            @AuthenticationPrincipal Jwt jwt
            ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.getUsersBySubscriptionId(subscriptionId, jwtModel));
    }

    @PostMapping("/subscription/{subscriptionId}/user")
    public ResponseEntity<UserDataDTO> addUserToSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody UserDataDTO userDataDTO,
            @AuthenticationPrincipal Jwt jwt
            ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.addUserToSubscription(subscriptionId, jwtModel, userDataDTO.getUserName()));
    }

    @DeleteMapping("/subscription/{subscriptionId}/user/{userId}")
    public ResponseEntity<Void> removeUserFromSubscription(
            @PathVariable Long subscriptionId,
            @PathVariable Long userId,
            @AuthenticationPrincipal Jwt jwt
            ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        userDataService.removeUserFromSubscription(subscriptionId, userId, jwtModel);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subscription/{subscriptionId}/user/create")
    public ResponseEntity<UserDataDTO> createUserAndAddToSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody UserDataDTO userDataDTO,
            @AuthenticationPrincipal Jwt jwt
            ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.createUserAndSubscription(subscriptionId, jwtModel, userDataDTO));
    }


}
