package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Organisation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Optional<Organisation> findByReferenceId(String referenceId);
    List<Organisation> findByName(String name);
    List<Organisation> findByShortName(String shortName);
    Optional<Organisation> findByVatNumber(String vatNumber);

    @Query("SELECT o FROM Organisation o WHERE LOWER(o.name) LIKE %:query% OR LOWER(o.shortName) LIKE %:query% OR LOWER(o.vatNumber) LIKE %:query% ORDER BY o.id ASC")
    List<Organisation> search(String query, Pageable pageable);

    @Query("SELECT COUNT(o.id) FROM Organisation o WHERE LOWER(o.name) LIKE %:query% OR LOWER(o.shortName) LIKE %:query% OR LOWER(o.vatNumber) LIKE %:query%")
    Long countSearch(String query);
}
