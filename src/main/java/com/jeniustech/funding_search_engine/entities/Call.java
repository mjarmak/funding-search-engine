package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
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
    private String description;

    @Column(name = "description_display")
    private String displayDescription;

    @Column(length = 25000)
    private String destinationDetails;

    @Column(length = 25000)
    private String missionDetails;

    private String actionType;

    @Enumerated(EnumType.ORDINAL)
    private SubmissionProcedureEnum submissionProcedure;

    private Timestamp submissionDeadlineDate;
    private Timestamp submissionDeadlineDate2;

    private Timestamp openDate;

    private BigDecimal budgetMin;
    private BigDecimal budgetMax;

    private Short projectNumber;

    @Column(length = 50)
    private String pathId;

    @Column(length = 150)
    private String reference;

    private String typeOfMGA;
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
        displayDescription = this.description.substring(0, Math.min(this.description.length(), displayDescriptionMaxLength));
        return displayDescription;
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
}
