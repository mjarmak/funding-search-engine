package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisation_project_join")
public class OrganisationProjectJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Enumerated(EnumType.ORDINAL)
    private OrganisationProjectJoinTypeEnum type;

    @Column(name = "funding_organisation")
    private BigDecimal fundingOrganisation;

    @Column(name = "funding_eu")
    private BigDecimal fundingEU;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

}
