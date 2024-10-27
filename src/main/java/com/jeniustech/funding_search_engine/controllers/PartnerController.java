package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.PartnerQueryTypeEnum;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.ExportService;
import com.jeniustech.funding_search_engine.services.PartnerService;
import com.jeniustech.funding_search_engine.services.UserDataService;
import com.jeniustech.funding_search_engine.services.solr.PartnerSolrClientService;
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
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController implements IDataController<PartnerDTO> {

    private final PartnerService partnerService;
    private final UserDataService userDataService;
    private final ExportService exportService;
    private final PartnerSolrClientService partnerSolrClientService;

    @GetMapping("/{id}/graph/network")
    public ResponseEntity<PartnerDTO> getGraphMesh(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(partnerService.getGraphMesh(id));
    }

    @GetMapping("/search")
    public ResponseEntity<SearchDTO<PartnerDTO>> search(
            @RequestParam @Size(min = 2, max = 255) String query,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "NAME") PartnerQueryTypeEnum queryType,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        if (queryType.equals(PartnerQueryTypeEnum.NAME)) {
            return ResponseEntity.ok(
                    partnerSolrClientService.search(
                            query,
                            pageNumber,
                            pageSize,
                            jwtModel
                    ));
        } else {
            return ResponseEntity.ok(partnerService.searchByTopic(
                    jwtModel.getUserId(),
                    query
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerDTO> getById(@PathVariable Long id,
                                              @AuthenticationPrincipal Jwt jwt) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(partnerService.getDTOById(id, jwtModel.getUserId()));
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

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectDTO>> getProjects(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(partnerService.getProjectsByPartnerId(id));
    }

    @PostMapping("/excel")
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

    @GetMapping("/{id}/projects/excel")
    public ResponseEntity<InputStreamResource> getProjectsExcel(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        JwtModel jwtModel = UserDataMapper.map(jwt);

        ByteArrayInputStream in = exportService.generatePartnerProjectExcel(id, jwtModel.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

}
