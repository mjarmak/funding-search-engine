package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.FrameworkProgramEnum;
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
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Slf4j
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

    @Enumerated(EnumType.ORDINAL)
    private FrameworkProgramEnum frameworkProgram;

    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Call call;

    private String callIdentifier;
    private String masterCallIdentifier;

    private String legalBasis;

    @Enumerated(EnumType.ORDINAL)
    private FundingSchemeEnum fundingScheme;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LongText> longTexts;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, orphanRemoval = true)
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

    public String getFrameworkProgramName() {
        return frameworkProgram != null ? frameworkProgram.getName() : null;
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
        return DateMapper.formatToDisplay(startDate);
    }
    public String getEndDateDisplay() {
        return DateMapper.formatToDisplay(endDate);
    }
    public String getSignDateDisplay() {
        return DateMapper.formatToDisplay(signDate);
    }

    public String getStatusName() {
        return status != null ? status.getName() : null;
    }

    public String getFundingSchemeName() {
        return fundingScheme != null ? fundingScheme.getName() : null;
    }

    public static boolean isFieldsValid(Project project) {
        if (project == null) {
            return true;
        }
        return project.isFieldsValid();
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
        if (this.getAcronym() != null && this.getAcronym().length() > 127) {
            log.warn("Acronym is too long: " + this.getAcronym());
            iValid = false;
        }
        if (this.getTitle() != null && this.getTitle().length() > 511) {
            log.warn("Title is too long: " + this.getTitle());
            iValid = false;
        }
        if (this.getLegalBasis() != null && this.getLegalBasis().length() > 100) {
            log.warn("Legal Basis is too long: " + this.getLegalBasis());
            iValid = false;
        }
        if (this.getMasterCallIdentifier() != null && this.getMasterCallIdentifier().length() > 63) {
            log.warn("Master Call Identifier is too long: " + this.getMasterCallIdentifier());
            iValid = false;
        }
        if (this.getCallIdentifier() != null && this.getCallIdentifier().length() > 127) {
            log.warn("Call Identifier is too long: " + this.getCallIdentifier());
            iValid = false;
        }
        return iValid; // All validations passed
    }

}
