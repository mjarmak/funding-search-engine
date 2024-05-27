package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.ActionTypeEnum;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SolrDocument(collection = "calls")
public class CallDocument {

    @Id
    @Indexed(name = "id")
    private Long id;

    @Indexed(name = "identifier")
    private String identifier;

    @Indexed(name = "title")
    private String title;

    @Indexed(name = "description")
    private String description;

    @Indexed(name = "action_type")
    private ActionTypeEnum actionType;

    @Indexed(name = "submission_deadline_date")
    private Timestamp submissionDeadlineDate;

    @Indexed(name = "open_date")
    private Timestamp openDate;

    @Indexed(name = "budget")
    private String budget;

    @Indexed(name = "project_number")
    private Short projectNumber;


}
