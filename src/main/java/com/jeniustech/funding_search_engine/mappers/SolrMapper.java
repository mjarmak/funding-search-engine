package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.util.StringUtil;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.constants.Constants.*;

public interface SolrMapper {

    static SolrInputDocument map(Call call) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, call.getId());
        document.addField(IDENTIFIER, call.getIdentifier());
        if (StringUtil.isNotEmpty(call.getTitle())) {
            document.addField(TITLE, call.getTitle());
        }
        if (StringUtil.isNotEmpty(call.getLongTextsToString())) {
            document.addField(LONG_TEXT, call.getLongTextsToString());
        }
        if (StringUtil.isNotEmpty(call.getActionType())) {
            document.addField(ACTION_TYPE, call.getActionType());
        }
        if (StringUtil.isNotEmpty(call.getEndDate())) {
            document.addField(END_DATE, DateMapper.mapToSolrString(call.getEndDate()));
        }
        if (StringUtil.isNotEmpty(call.getEndDate2())) {
            document.addField(END_DATE_2, DateMapper.mapToSolrString(call.getEndDate2()));
        }
        if (StringUtil.isNotEmpty(call.getStartDate())) {
            document.addField(START_DATE, DateMapper.mapToSolrString(call.getStartDate()));
        }
        if (StringUtil.isNotEmpty(call.getBudgetMin())) {
            document.addField(BUDGET_MIN, call.getBudgetMinString());
        }
        if (StringUtil.isNotEmpty(call.getBudgetMax())) {
            document.addField(BUDGET_MAX, call.getBudgetMaxString());
        }
        if (StringUtil.isNotEmpty(call.getProjectNumber())) {
            document.addField(PROJECT_NUMBER, call.getProjectNumber());
        }
        return document;
    }

    static CallDTO map(SolrDocument solrDocument) {
        return CallDTO.builder()
                .id((Long) solrDocument.getFieldValue(ID))
                .identifier((String) solrDocument.getFieldValue(IDENTIFIER))
                .title((String) solrDocument.getFieldValue(TITLE))
                .actionType((String) solrDocument.getFieldValue(ACTION_TYPE))
                .endDate(DateMapper.map((Date) solrDocument.getFieldValue(END_DATE)))
                .startDate(DateMapper.map((Timestamp) solrDocument.getFieldValue(START_DATE)))
                .budgetMin((String) solrDocument.getFieldValue(BUDGET_MIN))
                .budgetMax((String) solrDocument.getFieldValue(BUDGET_MAX))
                .projectNumber(Optional.of(solrDocument.getFieldValue(PROJECT_NUMBER))
                        .map(Integer.class::cast)
                        .map(Integer::shortValue)
                        .orElse(null))
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
