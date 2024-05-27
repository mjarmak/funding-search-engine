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
    private ActionTypeEnum actionType;
    private Timestamp submissionDeadlineDate;

    @Column(name = "submission_deadline2_date")
    private Timestamp submissionDeadline2Date;
    private Timestamp openDate;
    private String budget;

    private Short projectNumber;


    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Version
    private Integer version;

    public CallDocument toDocument() {
        return CallDocument.builder()
                .id(this.id)
                .identifier(this.identifier)
                .title(this.title)
                .description(this.description)
                .actionType(this.actionType)
                .submissionDeadlineDate(this.submissionDeadlineDate)
                .openDate(this.openDate)
                .budget(this.budget)
                .projectNumber(this.projectNumber)
                .build();
    }

}
