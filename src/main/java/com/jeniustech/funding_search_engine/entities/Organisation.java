package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.BooleanEnum;
import com.jeniustech.funding_search_engine.enums.ContactInfoTypeEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.mappers.NumberMapper;
import com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil;
import com.jeniustech.funding_search_engine.util.StringUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
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
    private String referenceId;
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

    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum sme;

    @Enumerated(EnumType.ORDINAL)
    private OrganisationTypeEnum type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", fetch = FetchType.LAZY)
    private List<OrganisationContactInfo> contactInfos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organisation", fetch = FetchType.LAZY)
    private List<OrganisationProjectJoin> organisationProjectJoins;

    @Column(name = "funding_organisation")
    private BigDecimal fundingOrganisation;

    @Column(name = "funding_eu")
    private BigDecimal fundingEU;

    private Short projectNumber;

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

    public boolean isSme() {
        if (sme == null) {
            return false;
        }
        return BooleanEnum.TRUE.equals(this.sme);
    }

    public String getFundingOrganisationDisplayString() {
        if (fundingOrganisation == null || fundingOrganisation.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return NumberMapper.shortenNumber(fundingOrganisation, 1);
    }
    public String getFundingEUDisplayString() {
        if (fundingEU == null || fundingEU.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return NumberMapper.shortenNumber(fundingEU, 1);
    }
    public String getInnovilyseUrl() {
        return UrlTypeEnum.getInnovilyseUrl("partner", id);
    }

    public String getAddressString() {
        if (address == null) {
            return null;
        }
        return address.toString();
    }

    public String getCountryCode() {
        if (address == null || address.getCountry() == null) {
            return null;
        }
        return address.getCountry().name();
    }

    public String getTypeName() {
        if (type == null) {
            return null;
        }
        return type.getDisplayName();
    }

    public String getWebSiteUrl() {
        if (contactInfos == null) {
            return null;
        }
        return contactInfos.stream()
                .filter(contactInfo -> contactInfo.getType() == ContactInfoTypeEnum.URL)
                .findFirst().map(OrganisationContactInfo::getValue)
                .orElse(null);
    }

    public List<Project> getProjects() {
        if (organisationProjectJoins == null || organisationProjectJoins.isEmpty()) {
            return null;
        }
        return organisationProjectJoins.stream()
                .map(OrganisationProjectJoin::getProject)
                .toList();
    }

    public boolean isDifferent(Organisation other) {

        if (!ScraperStringUtil.isDifferent(other.getVatNumber(), this.getVatNumber(), true)) {
            return false;
        } else if (ScraperStringUtil.isDifferent(other.getVatNumber(), this.getVatNumber(), false)) {
            return true;
        } else if (!ScraperStringUtil.isDifferent(other.getReferenceId(), this.getReferenceId(), true)) {
            return false;
        } else if (ScraperStringUtil.isDifferent(other.getReferenceId(), this.getReferenceId(), false)) {
            return true;
        }

        return ScraperStringUtil.isDifferent(other.getId(), this.getId())
                || ScraperStringUtil.isDifferent(other.getName(), this.getName(), false)
                || ScraperStringUtil.isDifferent(other.getShortName(), this.getShortName(), false);
    }

    public String getFundingOrganisationString() {
        if (fundingOrganisation == null || fundingOrganisation.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return fundingOrganisation.stripTrailingZeros().toPlainString();
    }
    public String getFundingEUString() {
        if (fundingEU == null || fundingEU.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return fundingEU.stripTrailingZeros().toPlainString();
    }

    public boolean isFieldsValid() {
        boolean iValid = true;
        if (this.getReferenceId() != null && this.getReferenceId().length() > 63) {
            log.warn("Reference ID is too long: " + this.getReferenceId());
            iValid = false;
        }
        if (this.getRcn() != null && this.getRcn().length() > 63) {
            log.warn("RCN is too long: " + this.getRcn());
            iValid = false;
        }
        if (this.getName() != null && this.getName().length() > 255) {
            log.warn("Name is too long: " + this.getName());
            iValid = false;
        }
        if (this.getShortName() != null && this.getShortName().length() > 255) {
            log.warn("Short name is too long: " + this.getShortName());
            iValid = false;
        }
        if (this.getVatNumber() != null && this.getVatNumber().length() > 63) {
            log.warn("VAT number is too long: " + this.getVatNumber());
            iValid = false;
        }
        if (this.getNutsCode() != null && this.getNutsCode().length() > 15) {
            log.warn("NUTS code is too long: " + this.getNutsCode());
            iValid = false;
        }
        if (this.getContactInfos() != null) {
            for (OrganisationContactInfo contactInfo : this.getContactInfos()) {
                if (contactInfo.getValue() != null && contactInfo.getValue().length() > 255) {
                    log.warn("Contact info value is too long: " + contactInfo.getValue());
                    iValid = false;
                }
                if (contactInfo.getName() != null && contactInfo.getName().length() > 127) {
                    log.warn("Contact info name is too long: " + contactInfo.getName());
                    iValid = false;
                }
            }
        }
        if (this.getAddress() != null) {
            if (address.getStreet() != null && address.getStreet().length() > 255) {
                log.warn("Address street is too long: " + address.getStreet());
                iValid = false;
            }
            if (address.getCity() != null && address.getCity().length() > 255) {
                log.warn("Address city is too long: " + address.getCity());
                iValid = false;
            }
            if (address.getPostCode() != null && address.getPostCode().length() > 31) {
                log.warn("Address postal code is too long: " + address.getPostCode());
                iValid = false;
            }
        }
        return iValid; // All validations passed
    }

}
