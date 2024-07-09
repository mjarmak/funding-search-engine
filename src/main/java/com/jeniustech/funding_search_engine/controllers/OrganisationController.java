package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.OrganisationDTO;
import com.jeniustech.funding_search_engine.services.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    @GetMapping("/organisation/{id}")
    public OrganisationDTO getOrganisationById(
            @PathVariable Long id) {
        return organisationService.getOrganisationById(id);
    }

}
