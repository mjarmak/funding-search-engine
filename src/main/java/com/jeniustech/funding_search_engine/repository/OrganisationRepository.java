package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Optional<Organisation> findByReferenceId(String referenceId);
    List<Organisation> findByName(String name);
    List<Organisation> findByShortName(String shortName);
    Optional<Organisation> findByVatNumber(String vatNumber);
}
