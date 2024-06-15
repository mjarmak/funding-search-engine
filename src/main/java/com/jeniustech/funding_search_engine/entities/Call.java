package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

import static com.jeniustech.funding_search_engine.constants.Constants.displayDescriptionMaxLength;

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

    private String identifier;
    private String title;

    @Column(length = 25000)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "call")
    private List<LongText> longTexts;

    @Column(name = "description_display")
    private String displayDescription;

    private String actionType;

    @Enumerated(EnumType.ORDINAL)
    private SubmissionProcedureEnum submissionProcedure;

    private Timestamp endDate;
    private Timestamp endDate2;

    private Timestamp startDate;

    private BigDecimal budgetMin;
    private BigDecimal budgetMax;

    private Short projectNumber;

    @Enumerated(EnumType.ORDINAL)
    private UrlTypeEnum urlType;

    @Column(length = 150)
    private String urlId;

    private String typeOfMGADescription;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Version
    private Integer version;

    public String getDisplayDescription() {
        if (displayDescription != null) {
            return displayDescription;
        }
        String priorityDescription = this.longTexts.stream()
                .filter(longText -> longText.getType().equals(LongTextTypeEnum.DESCRIPTION))
                .findFirst()
                .map(LongText::getText)
                .orElse(
                        this.longTexts.stream()
                                .filter(longText -> longText.getType().equals(LongTextTypeEnum.FURTHER_INFORMATION))
                                .findFirst()
                                .map(LongText::getText)
                                .orElse(
                                        this.longTexts.stream()
                                                .filter(longText -> longText.getType().equals(LongTextTypeEnum.BENEFICIARY_ADMINISTRATION))
                                                .findFirst()
                                                .map(LongText::getText)
                                                .orElse("")
                                )
                );

        displayDescription = priorityDescription.substring(0, Math.min(priorityDescription.length(), displayDescriptionMaxLength));
        return displayDescription;
    }

    public String getLongTextsToString() {
        StringBuilder longTextsString = new StringBuilder();
        for (LongText longText : longTexts) {
            longTextsString.append(longText.getText()).append("\n");
        }
        return longTextsString.toString();
    }

    // format to millions
    public static String processBudget(BigDecimal budget) {
        if (budget == null) {
            return null;
        }
        return budget.divide(new BigDecimal(1000000), 2, RoundingMode.HALF_EVEN).toString();
    }

    public String getBudgetString() {
        return processBudget(budgetMin) + "-" + processBudget(budgetMax) + "M";
    }

    public String getUrl() {
        return urlType.getUrl(urlId);
    }
}
