package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import com.jeniustech.funding_search_engine.util.StringUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    private BigDecimal fundingOrganisation;
    private BigDecimal fundingEU;

    private ProjectStatusEnum status;

    private Timestamp signDate;
    private Timestamp startDate;
    private Timestamp endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Call call;

    private String masterCallIdentifier;

    private String legalBasis;

    @Enumerated(EnumType.ORDINAL)
    private FundingSchemeEnum fundingScheme;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<LongText> longTexts;

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

}
