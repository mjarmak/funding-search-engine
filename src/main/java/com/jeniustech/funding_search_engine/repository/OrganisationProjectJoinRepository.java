package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganisationProjectJoinRepository extends JpaRepository<OrganisationProjectJoin, Long> {
    @Query("SELECT opj.organisation.id FROM OrganisationProjectJoin opj WHERE opj.project.id IN ?1")
    List<OrganisationProjectJoin> findAllByProjectIds(List<Long> ids);
}
