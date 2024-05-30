package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.ActionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

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

    @Column(name = "description", length = 10240)
    private String description;

    @Column(name = "description_display", length = 10240)
    private String displayDescription;

    private ActionTypeEnum actionType;
    private LocalDate submissionDeadlineDate;

    @Column(name = "submission_deadline2_date")
    private LocalDate submissionDeadline2Date;
    private LocalDate openDate;
    private String budget;

    private Short projectNumber;


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

    public String getActionTypeName() {
        return Objects.requireNonNullElse(this.actionType, ActionTypeEnum.UNKNOWN).getName();
    }

    public static String processBudget(String budget) {
        if (budget == null) {
            return null;
        }
        return budget
                .toLowerCase()
                .replace("to", "-")
                .replace(" ", "")
                .replace("\n","")
                .replace("\t","")
                .replace(".00","")
                .replace("around","")
                .trim();
    }
}
