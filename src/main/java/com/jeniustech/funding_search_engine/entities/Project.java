package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.NumberMapper;
import com.jeniustech.funding_search_engine.util.StringUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String referenceId;
    private String rcn;

    private String acronym;
    private String title;

    @Column(name = "funding_organisation")
    private BigDecimal fundingOrganisation;

    @Column(name = "funding_eu")
    private BigDecimal fundingEU;

    @Enumerated(EnumType.ORDINAL)
    private ProjectStatusEnum status;

    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Call call;

    private String masterCallIdentifier;

    private String legalBasis;

    @Enumerated(EnumType.ORDINAL)
    private FundingSchemeEnum fundingScheme;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", fetch = FetchType.LAZY)
    private List<LongText> longTexts;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<OrganisationProjectJoin> organisationProjectJoins;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Version
    private Integer version;

    public String toString() {
        return "Project(id=" + this.getId() + ", title=" + this.getTitle() + ", referenceId=" + this.getReferenceId() + ", rcn=" + this.getRcn() + ", acronym=" + this.getAcronym()
                + ")";
    }

    public String getLongTextsToString() {
        StringBuilder longTextsString = new StringBuilder();
        for (LongText longText : longTexts) {
            longTextsString.append(longText.getText()).append("\n");
        }
        String result = longTextsString.toString();
        if (StringUtil.isNotEmpty(result)) {
            return result;
        } else {
            return null;
        }
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
        return UrlTypeEnum.getInnovilyseUrl("project", id);
    }
    public String getUrl() {
        return "https://cordis.europa.eu/project/id/" + referenceId;
    }

    public String getStartDateDisplay() {
        return DateMapper.formatToDisplay(startDate.atStartOfDay());
    }
    public String getEndDateDisplay() {
        return DateMapper.formatToDisplay(endDate.atStartOfDay());
    }
    public String getSignDateDisplay() {
        return DateMapper.formatToDisplay(signDate.atStartOfDay());
    }

    public String getStatusName() {
        return status != null ? status.getName() : null;
    }

    public String getFundingSchemeName() {
        return fundingScheme != null ? fundingScheme.getName() : null;
    }
}
