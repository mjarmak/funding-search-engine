package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.enums.StatusFilterEnum;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.ProjectService;
import com.jeniustech.funding_search_engine.services.UserDataService;
import com.jeniustech.funding_search_engine.services.solr.ProjectSolrClientService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController implements IDataController<ProjectDTO> {

    private final ProjectService projectService;
    private final UserDataService userDataService;
    private final ProjectSolrClientService projectSolrClientService;

    @GetMapping("/search")
    public ResponseEntity<SearchDTO<ProjectDTO>> search(
            @RequestParam @Size(min = 2, max = 255) String query,
            @RequestParam(required = true, defaultValue = "0") int pageNumber,
            @RequestParam(required = true, defaultValue = "20") int pageSize,
            @RequestParam(required = false, name = "status", defaultValue = "UPCOMING,OPEN,CLOSED"
            ) List<StatusFilterEnum> statusFilters,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(projectSolrClientService.search(
                query,
                pageNumber,
                pageSize,
                statusFilters,
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

}
