package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.BooleanEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import com.jeniustech.funding_search_engine.util.StringUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_coordinates_id")
    private LocationCoordinates locationCoordinates;

    private String vatNumber;
    private String nutsCode;

    private BooleanEnum sme;

    private OrganisationTypeEnum type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", fetch = FetchType.LAZY)
    private List<OrganisationContactInfo> contactInfos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", fetch = FetchType.LAZY)
    private List<OrganisationProjectJoin> organisationProjectJoins;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Version
    private Integer version;

    public String toString() {
        return "Organisation(id=" + this.getId() + ", name=" + this.getName() + ")";
    }

    public String getShortNameOrName() {
        return StringUtil.isNotEmpty(this.shortName) ? this.shortName : this.name;
    }
}
