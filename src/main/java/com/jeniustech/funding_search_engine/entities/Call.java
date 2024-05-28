package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.ActionTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
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

    public SolrInputDocument toSolrDocument() {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", this.id);
        document.addField("identifier", this.identifier);
        document.addField("title", this.title);
        document.addField("description_display", this.description.substring(0, Math.min(this.description.length(), displayDescriptionMaxLength)));
        document.addField("description", this.description);
        document.addField("action_type", this.getActionTypeName());
        document.addField("submission_deadline_date", this.submissionDeadlineDate);
        document.addField("open_date", this.openDate);
        document.addField("budget", 10F);
        document.addField("project_number", this.projectNumber);
        return document;
    }

    private String getActionTypeName() {
        return Objects.requireNonNullElse(this.actionType, ActionTypeEnum.UNKNOWN).getName();
    }

}
