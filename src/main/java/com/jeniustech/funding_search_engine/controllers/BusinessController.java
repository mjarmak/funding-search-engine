package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.BusinessInformationDTO;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.BusinessInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BusinessController {

    private final BusinessInformationService businessInformationService;

    @GetMapping("/business")
    public ResponseEntity<BusinessInformationDTO> getBusinessInformation(
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(businessInformationService.getBusinessInformation(jwtModel));
    }

    @PostMapping("/business")
    public ResponseEntity<BusinessInformationDTO> saveBusinessInformation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody BusinessInformationDTO businessInformationDTO
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(businessInformationService.saveBusinessInformation(jwtModel, businessInformationDTO));
    }


}
