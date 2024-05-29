package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.enums.ActionTypeEnum;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SolrMapper {


    public static final String ID = "id";
    public static final String IDENTIFIER = "identifier";
    public static final String TITLE = "title";
    public static final String DESCRIPTION_DISPLAY = "description_display";
    public static final String DESCRIPTION = "description";
    public static final String ACTION_TYPE = "action_type";
    public static final String SUBMISSION_DEADLINE_DATE = "submission_deadline_date";
    public static final String OPEN_DATE = "open_date";
    public static final String BUDGET = "budget";
    public static final String PROJECT_NUMBER = "project_number";

    public static SolrInputDocument map(Call call) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, call.getId());
        document.addField(IDENTIFIER, call.getIdentifier());
        document.addField(TITLE, call.getTitle());
        document.addField(DESCRIPTION_DISPLAY, call.getDisplayDescription());
        document.addField(DESCRIPTION, call.getDescription());
        document.addField(ACTION_TYPE, call.getActionTypeName());
        document.addField(SUBMISSION_DEADLINE_DATE, DateMapper.map(call.getSubmissionDeadlineDate()));
        document.addField(OPEN_DATE, DateMapper.map(call.getOpenDate()));
        document.addField(BUDGET, call.getBudget());
        document.addField(PROJECT_NUMBER, call.getProjectNumber());
        return document;
    }

    public static CallDTO map(SolrDocument solrDocument) {
        return CallDTO.builder()
                .id((Long) solrDocument.getFieldValue(ID))
                .identifier((String) solrDocument.getFieldValue(IDENTIFIER))
                .title((String) solrDocument.getFieldValue(TITLE))
                .description((String) solrDocument.getFieldValue(DESCRIPTION))
                .displayDescription((String) solrDocument.getFieldValue(DESCRIPTION_DISPLAY))
                .actionType(ActionTypeEnum.valueOf((String) solrDocument.getFieldValue(ACTION_TYPE)))
                .submissionDeadlineDate((Date) solrDocument.getFieldValue(SUBMISSION_DEADLINE_DATE))
                .openDate((Date) solrDocument.getFieldValue(OPEN_DATE))
                .budget((String) solrDocument.getFieldValue(BUDGET))
                .projectNumber(Optional.of(solrDocument.getFieldValue(PROJECT_NUMBER))
                        .map(Integer.class::cast)
                        .map(Integer::shortValue)
                        .orElse(null))
                .build(
        );
    }

    public static List<CallDTO> map(List<SolrDocument> solrDocuments) {
        if (solrDocuments == null) {
            return null;
        }
        return solrDocuments.stream()
                .map(SolrMapper::map)
                .toList();
    }

}
