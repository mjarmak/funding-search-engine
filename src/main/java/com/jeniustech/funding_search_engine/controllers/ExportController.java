package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.exceptions.ReportException;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.ExportService;
import com.jeniustech.funding_search_engine.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final ReportService reportService;

    @PostMapping("call/excel")
    public ResponseEntity<InputStreamResource> downloadCallExcel(
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generateCallExcel(ids, jwtModel.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
    @PostMapping("project/excel")
    public ResponseEntity<InputStreamResource> downloadProjectExcel(
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generateProjectExcel(ids, jwtModel.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
    @PostMapping("partner/excel")
    public ResponseEntity<InputStreamResource> downloadPartnerExcel(
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generatePartnerExcel(ids, jwtModel.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("call/{callId}/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(
            @PathVariable Long callId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String imageUrl
    ) throws ReportException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream bis = reportService.generatePdf(List.of(callId), jwtModel.getUserId(), imageUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=data.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @PostMapping("/call/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(
            @RequestBody List<Long> callIds,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String imageUrl
    ) throws ReportException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream bis = reportService.generatePdf(callIds, jwtModel.getUserId(), imageUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=data.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
