package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.BooleanEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisations")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long referenceId;
    private String rcn;

    private String name;
    private String shortName;

    private BigDecimal fundingOrganisation;
    private BigDecimal fundingEU;

    @OneToOne
    private Address address;

    @OneToOne
    private LocationCoordinates locationCoordinates;

    private String vatNumber;
    private String nutsCode;

    private BooleanEnum sme;

    private OrganisationTypeEnum type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", fetch = FetchType.LAZY)
    private List<OrganisationContactInfo> contactInfos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", fetch = FetchType.LAZY)
    private List<OrganisationProjectJoin> organisationProjectJoins;
}
