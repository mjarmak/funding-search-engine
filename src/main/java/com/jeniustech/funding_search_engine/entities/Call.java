package com.jeniustech.funding_search_engine.entities;

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

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 25000)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "call")
    private List<LongText> longTexts;

    @Column(length = 255)
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

    @Column(name = "type_of_mga_description", length = 255)
    private String typeOfMGADescription;

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
        if (budgetMin == null && budgetMax == null) {
            return "N/A";
        } else if (budgetMin == null) {
            return "<" + NumberMapper.formatNumberWithCommas(budgetMax);
        } else if (budgetMax == null) {
            return ">" + NumberMapper.formatNumberWithCommas(budgetMin);
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
        return budgetMin.stripTrailingZeros().toPlainString();
    }

    public String getBudgetMaxString() {
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

    public String getUrl() {
        return urlType.getUrl(identifier, urlId);
    }

    public String getEndDate2Display() {
        return DateMapper.formatToDisplay(endDate2);
    }

    public String getStartDateDisplay() {
        return DateMapper.formatToDisplay(startDate);
    }

    public String getEndDateDisplay() {
        return DateMapper.formatToDisplay(endDate);
    }
}
