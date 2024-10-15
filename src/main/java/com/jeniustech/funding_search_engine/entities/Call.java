package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
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
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calls")
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String identifier;

    @Column(length = 150, nullable = false)
    private String reference;

    @Column(nullable = false)
    private String title;

    private String keywords;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "call", fetch = FetchType.LAZY)
    private List<LongText> longTexts;

    private String actionType;

    @Enumerated(EnumType.ORDINAL)
    private SubmissionProcedureEnum submissionProcedure;

    private Timestamp endDate;

    @Column(name = "end_date_2")
    private Timestamp endDate2;

    private Timestamp startDate;

    private BigDecimal budgetMin;
    private BigDecimal budgetMax;

    private Short projectNumber;

    @Enumerated(EnumType.ORDINAL)
    private UrlTypeEnum urlType;

    @Column(length = 150)
    private String urlId;

    @Column(name = "type_of_mga_description")
    private String typeOfMGADescription;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "call")
    private List<Project> projects;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Version
    private Integer version;

    public String toString() {
        return "Call(id=" + this.getId() + ", identifier=" + this.getIdentifier() + ", title=" + this.getTitle() + ", startDate=" + this.getStartDate() + ", endDate=" + this.getEndDate() + ", endDate2=" + this.getEndDate2() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", version=" + this.getVersion() + ")";
    }

    public String getBudgetRangeString() {
        return getBudgetRangeString(budgetMin, budgetMax);
    }

    public static String getBudgetRangeString(BigDecimal budgetMin, BigDecimal budgetMax) {
        if (budgetMin == null && budgetMax == null) {
            return "N/A";
        } else if (budgetMin == null) {
            return "<" + NumberMapper.formatNumberWithCommas(budgetMax);
        } else if (budgetMax == null) {
            return ">" + NumberMapper.formatNumberWithCommas(budgetMin);
        } else if (budgetMin.compareTo(budgetMax) == 0) {
            return NumberMapper.formatNumberWithCommas(budgetMin);
        }
        return NumberMapper.formatNumberWithCommas(budgetMin) + " - " + NumberMapper.formatNumberWithCommas(budgetMax);
    }

    public String getBudgetMinDisplayString() {
        return NumberMapper.shortenNumber(budgetMin, 1);
    }

    public String  getBudgetMaxDisplayString() {
        return NumberMapper.shortenNumber(budgetMax, 1);
    }

    public String getBudgetMinString() {
        if (budgetMin == null) {
            return null;
        }
        return budgetMin.stripTrailingZeros().toPlainString();
    }

    public String getBudgetMaxString() {
        if (budgetMax == null) {
            return null;
        }
        return budgetMax.stripTrailingZeros().toPlainString();
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

    public String getAllText() {
        String result = identifier + " " + title;
        if (getLongTextsToString() != null) {
            result = result + " " + getLongTextsToString();
        }
        return result;
    }

    public String getDescription() {
        if (longTexts != null) {
            return longTexts.stream().filter(longText -> longText.getType() == LongTextTypeEnum.DESCRIPTION).findFirst().map(LongText::getText).orElse(null);
        }
        return null;
    }

    public String getUrl() {
        if (reference != null && urlId == null) {
            var type = UrlTypeEnum.getType(reference);
            return UrlTypeEnum.getUrl(type, identifier, reference);
        }
        return urlType.getUrl(identifier, urlId);
    }

    public String getInnovilyseUrl() {
        return UrlTypeEnum.getInnovilyseUrl("call", id);
    }

    public String getEndDate2Display(String timezone) {
        return DateMapper.formatToDisplay(DateMapper.convertParisToLocal(DateMapper.map(endDate2), timezone));
    }

    public String getStartDateDisplay(String timezone) {
        return DateMapper.formatToDisplay(DateMapper.convertParisToLocal(DateMapper.map(startDate), timezone));
    }

    public String getEndDateDisplay(String timezone) {
        return DateMapper.formatToDisplay(DateMapper.convertParisToLocal(DateMapper.map(endDate), timezone));
    }

    public LongText getLongTextByType(LongTextTypeEnum type) {
        if (longTexts != null) {
            return longTexts.stream().filter(longText -> longText.getType() == type).findFirst().orElse(null);
        }
        return null;
    }
    public String getLongTextByTypeAsString(LongTextTypeEnum type) {
        LongText longText = getLongTextByType(type);
        if (longText != null) {
            return longText.getText();
        }
        return null;
    }
}
