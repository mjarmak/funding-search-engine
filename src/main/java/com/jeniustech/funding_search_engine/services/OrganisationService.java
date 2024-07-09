package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.OrganisationDTO;
import com.jeniustech.funding_search_engine.exceptions.OrganisationNotFoundException;
import com.jeniustech.funding_search_engine.mappers.OrganisationMapper;
import com.jeniustech.funding_search_engine.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    public OrganisationDTO getOrganisationById(Long id) {
        return OrganisationMapper.mapToDTO(organisationRepository.findById(id).orElseThrow(OrganisationNotFoundException::new));
    }

}
