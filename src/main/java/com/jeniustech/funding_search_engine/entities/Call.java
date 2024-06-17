package com.jeniustech.funding_search_engine.entities;

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

//    public String getDisplayDescription() {
//        if (displayDescription != null) {
//            return displayDescription;
//        }
//        String priorityDescription = this.longTexts.stream()
//                .filter(longText -> longText.getType().equals(LongTextTypeEnum.DESCRIPTION))
//                .findFirst()
//                .map(LongText::getText)
//                .orElse(
//                        this.longTexts.stream()
//                                .filter(longText -> longText.getType().equals(LongTextTypeEnum.FURTHER_INFORMATION))
//                                .findFirst()
//                                .map(LongText::getText)
//                                .orElse(
//                                        this.longTexts.stream()
//                                                .filter(longText -> longText.getType().equals(LongTextTypeEnum.BENEFICIARY_ADMINISTRATION))
//                                                .findFirst()
//                                                .map(LongText::getText)
//                                                .orElse("")
//                                )
//                );
//
//        displayDescription = priorityDescription.substring(0, Math.min(priorityDescription.length(), displayDescriptionMaxLength));
//        return displayDescription;
//    }

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
        return urlType.getUrl(identifier, urlId);
    }
}
