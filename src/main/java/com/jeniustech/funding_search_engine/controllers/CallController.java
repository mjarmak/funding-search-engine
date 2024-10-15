package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.exceptions.ReportException;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.*;
import com.jeniustech.funding_search_engine.services.solr.CallSolrClientService;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/call")
@RequiredArgsConstructor
public class CallController implements IDataController<CallDTO> {

    private final CallService callService;
    private final PartnerService partnerService;
    private final ProjectService projectService;
    private final UserDataService userDataService;
    private final CallSolrClientService callSolrClientService;
    private final ExportService exportService;
    private final ReportService reportService;

    @GetMapping("/{id}/graph/network")
    public ResponseEntity<CallDTO> getGraphMesh(@PathVariable Long id) {
        return ResponseEntity.ok(callService.getGraphMesh(id));
    }

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

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectDTO>> getProjects(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(projectService.getProjectsByCallId(id));
    }

    @PostMapping("/excel")
    public ResponseEntity<InputStreamResource> downloadCallExcel(
            @RequestBody List<Long> ids,
            @RequestHeader(value = "X-User-Timezone", required = false, defaultValue = "Europe/Paris") String timezone,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generateCallExcel(ids, jwtModel.getUserId(), timezone);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PostMapping("/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(
            @RequestBody List<Long> callIds,
            @RequestHeader(value = "X-User-Timezone", required = false, defaultValue = "Europe/Paris") String timezone,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String imageUrl
    ) throws ReportException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream bis = reportService.generatePdf(callIds, jwtModel.getUserId(), imageUrl, timezone);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=data.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/{callId}/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(
            @PathVariable Long callId,
            @RequestHeader(value = "X-User-Timezone", required = false, defaultValue = "Europe/Paris") String timezone,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String imageUrl
    ) throws ReportException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream bis = reportService.generatePdf(List.of(callId), jwtModel.getUserId(), imageUrl, timezone);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=data.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/{id}/projects/excel")
    public ResponseEntity<InputStreamResource> getProjectsExcel(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Timezone", required = false, defaultValue = "Europe/Paris") String timezone,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generateCallProjectExcel(id, jwtModel.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }


}
