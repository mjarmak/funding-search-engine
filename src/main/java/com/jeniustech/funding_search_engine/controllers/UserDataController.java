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
public class UserDataController {

    private final UserDataService userDataService;

    @GetMapping("user/data")
    public ResponseEntity<UserDataDTO> getUserData(
            @AuthenticationPrincipal Jwt jwt
            ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.getUserDataOrCreate(jwtModel));
    }

    @GetMapping("/subscription/{subscriptionId}/users")
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
        return ResponseEntity.ok(userDataService.addUser(subscriptionId, jwtModel, userDataDTO));
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

    @GetMapping("/user/display")
    public ResponseEntity<UserDataDTO> getUserDataByUsername(
            @RequestParam(required = true) String username
            ) {
        return ResponseEntity.ok(userDataService.getUserDataByUsername(username));
    }

}
