package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.referenceId = :referenceId")
    Optional<Project> findByReferenceId(Long referenceId);

    @Query("SELECT p FROM Project p WHERE p.call.id = :id")
    List<Project> findAllByCallId(Long id);
}
