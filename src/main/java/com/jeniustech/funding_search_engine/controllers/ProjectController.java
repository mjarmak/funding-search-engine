package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.FrameworkProgramEnum;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.ExportService;
import com.jeniustech.funding_search_engine.services.ProjectService;
import com.jeniustech.funding_search_engine.services.UserDataService;
import com.jeniustech.funding_search_engine.services.solr.ProjectSolrClientService;
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
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController implements IDataController<ProjectDTO> {

    private final ProjectService projectService;
    private final UserDataService userDataService;
    private final ProjectSolrClientService projectSolrClientService;
    private final ExportService exportService;

    @GetMapping("/{id}/graph/network")
    public ResponseEntity<ProjectDTO> getGraphMesh(
            @PathVariable Long id) {
        return ResponseEntity.ok(projectService.getGraphMesh(id));
    }
    @GetMapping("/search")
    public ResponseEntity<SearchDTO<ProjectDTO>> search(
            @RequestParam @Size(min = 2, max = 255) String query,
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @RequestParam(required = false, name = "status", defaultValue = "UPCOMING,OPEN,CLOSED"
            ) List<StatusFilterEnum> statusFilters,
            @RequestParam(required = false, name = "program", defaultValue =
                    "HORIZON," +
                            "H2020," +
                            "FP1," +
                            "FP2," +
                            "FP3," +
                            "FP4," +
                            "FP5," +
                            "FP6," +
                            "FP7"
            ) List<FrameworkProgramEnum> programFilters,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(projectSolrClientService.search(
                query,
                pageNumber,
                pageSize,
                statusFilters,
                programFilters,
                jwtModel
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id,
                                              @AuthenticationPrincipal Jwt jwt) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(projectService.getDTOById(id, jwtModel.getUserId()));
    }

    @GetMapping("/{id}/favorite")
    public ResponseEntity<Void> favorite(@PathVariable Long id,
                                         @AuthenticationPrincipal Jwt jwt
                             ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        projectService.favorite(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> unFavorite(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt
                               ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        projectService.unFavorite(id, jwtModel.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<SearchDTO<ProjectDTO>> getFavorites(
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(projectService.getFavoritesByUserId(jwtModel.getUserId(), pageNumber, pageSize));
    }

    @GetMapping("/search/history")
    public ResponseEntity<List<String>> getSearchHistory(
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(userDataService.getSearchHistory(jwtModel.getUserId(), LogTypeEnum.SEARCH_PROJECT));
    }

    @GetMapping("/{id}/partners")
    public ResponseEntity<List<PartnerDTO>> getPartners(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(projectService.getPartnersByProjectId(id));
    }

    @PostMapping("/excel")
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

}
