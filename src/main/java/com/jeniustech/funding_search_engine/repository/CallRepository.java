package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Call;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    @Query("SELECT c FROM Call c WHERE c.reference = :reference")
    Optional<Call> findByReference(String reference);

    @Query("SELECT c FROM Call c WHERE c.identifier = :identifier ORDER BY c.budgetMax DESC")
    List<Call> findyIdentifier(String identifier);

    @Query("SELECT c FROM Call c WHERE c.createdAt >= :after")
    Page<Call> findAllAfter(Pageable pageable, Timestamp after);
}
