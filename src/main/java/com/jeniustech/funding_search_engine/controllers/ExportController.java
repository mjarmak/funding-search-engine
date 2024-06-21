package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/excel")
    public ResponseEntity<InputStreamResource> downloadExcel(
            @RequestBody List<Long> callIds,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generateExcel(callIds, jwtModel.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

}
