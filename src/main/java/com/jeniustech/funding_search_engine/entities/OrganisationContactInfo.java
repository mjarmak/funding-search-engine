package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.ContactInfoTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisation_contact_info")
public class OrganisationContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    @Enumerated(EnumType.ORDINAL)
    private ContactInfoTypeEnum type;

    private String name;
    private String value;

}
