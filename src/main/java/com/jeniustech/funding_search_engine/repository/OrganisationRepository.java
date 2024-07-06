package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    @Query("SELECT o FROM Organisation o WHERE o.referenceId = :referenceId")
    Optional<Organisation> findByReferenceId(Long referenceId);
}
