package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.OrganisationProjectJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganisationProjectJoinRepository extends JpaRepository<OrganisationProjectJoin, Long> {
    @Query("SELECT opj FROM OrganisationProjectJoin opj WHERE opj.project.id IN ?1")
    List<OrganisationProjectJoin> findAllByProjectIds(List<Long> ids);

    @Query("SELECT opj FROM OrganisationProjectJoin opj WHERE opj.project.id = ?1")
    List<OrganisationProjectJoin> findAllByProjectId(Long id);

    @Query("SELECT opj FROM OrganisationProjectJoin opj WHERE opj.organisation.id = ?1")
    List<OrganisationProjectJoin> findAllByPartnerId(Long id);
}
