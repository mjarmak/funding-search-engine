package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
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

    private Long referenceId;
    private String rcn;

    private String acronym;
    private String title;

    @Column(name = "funding_organisation")
    private BigDecimal fundingOrganisation;

    @Column(name = "funding_eu")
    private BigDecimal fundingEU;

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

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Version
    private Integer version;

    public String toString() {
        return "Project(id=" + this.getId() + ", title=" + this.getTitle() + ")";
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

    public String getFundingEUString() {
        return fundingEU.stripTrailingZeros().toPlainString();
    }
    public String getFundingOrganisationString() {
        return fundingOrganisation.stripTrailingZeros().toPlainString();
    }

}
