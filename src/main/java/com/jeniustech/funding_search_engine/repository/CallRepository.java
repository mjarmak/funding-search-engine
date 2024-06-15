package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    @Query("SELECT c FROM Call c WHERE c.identifier = :identifier AND c.startDate = :startDate AND c.endDate = :endDate")
    Optional<Call> findByIdentifierAndStartDateAndEndDate(String identifier, Timestamp startDate, Timestamp endDate);
}
