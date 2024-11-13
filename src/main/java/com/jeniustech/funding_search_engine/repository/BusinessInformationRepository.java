package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.BusinessInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessInformationRepository extends JpaRepository<BusinessInformation, Long> {
}
