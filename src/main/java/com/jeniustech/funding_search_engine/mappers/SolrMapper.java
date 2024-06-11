package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.constants.Constants.*;

public class SolrMapper {

    public static SolrInputDocument map(Call call) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, call.getId());
        document.addField(IDENTIFIER, call.getIdentifier());
        document.addField(TITLE, call.getTitle());
        document.addField(DESCRIPTION_DISPLAY, call.getDisplayDescription());
        document.addField(DESCRIPTION, call.getDescription());
        document.addField(DESTINATION_DETAILS, call.getDescription());
        document.addField(MISSION_DETAILS, call.getDescription());
        document.addField(ACTION_TYPE, call.getActionType());
        document.addField(SUBMISSION_DEADLINE_DATE, DateMapper.map(call.getSubmissionDeadlineDate()));
        document.addField(OPEN_DATE, DateMapper.map(call.getOpenDate()));
        document.addField(BUDGET, call.getBudgetString());
        document.addField(PROJECT_NUMBER, call.getProjectNumber());
        document.addField(PATH_ID, call.getPathId());
        document.addField(REFERENCE, call.getReference());
        return document;
    }

    public static CallDTO map(SolrDocument solrDocument) {
        return CallDTO.builder()
                .id((Long) solrDocument.getFieldValue(ID))
                .identifier((String) solrDocument.getFieldValue(IDENTIFIER))
                .title((String) solrDocument.getFieldValue(TITLE))
                .description((String) solrDocument.getFieldValue(DESCRIPTION))
                .displayDescription((String) solrDocument.getFieldValue(DESCRIPTION_DISPLAY))
                .actionType((String) solrDocument.getFieldValue(ACTION_TYPE))
                .submissionDeadlineDate(DateMapper.map((Date) solrDocument.getFieldValue(SUBMISSION_DEADLINE_DATE)))
                .openDate(DateMapper.map((Date) solrDocument.getFieldValue(OPEN_DATE)))
                .budget((String) solrDocument.getFieldValue(BUDGET))
                .projectNumber(Optional.of(solrDocument.getFieldValue(PROJECT_NUMBER))
                        .map(Integer.class::cast)
                        .map(Integer::shortValue)
                        .orElse(null))
                .pathId((String) solrDocument.getFieldValue(PATH_ID))
                .reference((String) solrDocument.getFieldValue(REFERENCE))
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
