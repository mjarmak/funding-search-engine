package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.constants.Constants.*;

public interface SolrMapper {

    default SolrInputDocument map(Call call) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, call.getId());
        document.addField(IDENTIFIER, call.getIdentifier());
        document.addField(TITLE, call.getTitle());
        document.addField(DESCRIPTION_DISPLAY, call.getDisplayDescription());
        document.addField(LONG_TEXT, call.getLongTextsToString());
        document.addField(ACTION_TYPE, call.getActionType());
        document.addField(SUBMISSION_DEADLINE_DATE, DateMapper.map(call.getEndDate()));
        document.addField(SUBMISSION_DEADLINE_DATE_2, DateMapper.map(call.getEndDate2()));
        document.addField(OPEN_DATE, DateMapper.map(call.getStartDate()));
        document.addField(BUDGET_MIN, call.getBudgetMin());
        document.addField(BUDGET_MAX, call.getBudgetMax());
        document.addField(PROJECT_NUMBER, call.getProjectNumber());
        document.addField(URL_ID, call.getUrlId());
        document.addField(URL_TYPE, call.getUrlType());
        return document;
    }

    static CallDTO map(SolrDocument solrDocument) {
        return CallDTO.builder()
                .id((Long) solrDocument.getFieldValue(ID))
                .identifier((String) solrDocument.getFieldValue(IDENTIFIER))
                .title((String) solrDocument.getFieldValue(TITLE))
                .displayDescription((String) solrDocument.getFieldValue(DESCRIPTION_DISPLAY))
                .actionType((String) solrDocument.getFieldValue(ACTION_TYPE))
                .endDate(DateMapper.map((Date) solrDocument.getFieldValue(SUBMISSION_DEADLINE_DATE)))
                .startDate(DateMapper.map((Timestamp) solrDocument.getFieldValue(OPEN_DATE)))
                .budgetMin((String) solrDocument.getFieldValue(BUDGET_MIN))
                .budgetMax((String) solrDocument.getFieldValue(BUDGET_MAX))
                .projectNumber(Optional.of(solrDocument.getFieldValue(PROJECT_NUMBER))
                        .map(Integer.class::cast)
                        .map(Integer::shortValue)
                        .orElse(null))
                .urlId((String) solrDocument.getFieldValue(URL_ID))
                .urlType(UrlTypeEnum.valueOf((String) solrDocument.getFieldValue(URL_TYPE)))
                .build(
        );
    }

    static List<CallDTO> map(List<SolrDocument> solrDocuments) {
        if (solrDocuments == null) {
            return null;
        }
        return solrDocuments.stream()
                .map(SolrMapper::map)
                .toList();
    }

}
