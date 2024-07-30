package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.constants.solr.CallColumns;
import com.jeniustech.funding_search_engine.constants.solr.ProjectColumns;
import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil;
import com.jeniustech.funding_search_engine.util.StringUtil;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.util.StringUtil.valueOrDefault;

public interface SolrMapper {

    static List<SolrInputDocument> mapCallsToSolrDocument(List<Call> calls) {
        if (calls == null) {
            return null;
        }
        return calls.stream()
                .map(SolrMapper::mapToSolrDocument)
                .toList();
    }
    static SolrInputDocument mapToSolrDocument(Call call) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(CallColumns.ID, call.getId());
        document.addField(CallColumns.IDENTIFIER, call.getIdentifier());
        if (StringUtil.isNotEmpty(call.getTitle())) {
            document.addField(CallColumns.TITLE, call.getTitle());
        }
        if (StringUtil.isNotEmpty(call.getLongTextsToString())) {
            document.addField(CallColumns.LONG_TEXT, ScraperStringUtil.removeHtmlTags(call.getLongTextsToString()));
        }
        if (StringUtil.isNotEmpty(call.getActionType())) {
            document.addField(CallColumns.ACTION_TYPE, call.getActionType());
        }
        if (StringUtil.isNotEmpty(call.getEndDate())) {
            document.addField(CallColumns.END_DATE, DateMapper.mapToSolrString(call.getEndDate()));
        }
        if (StringUtil.isNotEmpty(call.getEndDate2())) {
            document.addField(CallColumns.END_DATE_2, DateMapper.mapToSolrString(call.getEndDate2()));
        }
        if (StringUtil.isNotEmpty(call.getStartDate())) {
            document.addField(CallColumns.START_DATE, DateMapper.mapToSolrString(call.getStartDate()));
        }
        if (StringUtil.isNotEmpty(call.getBudgetMin())) {
            document.addField(CallColumns.BUDGET_MIN, call.getBudgetMinString());
        }
        if (StringUtil.isNotEmpty(call.getBudgetMax())) {
            document.addField(CallColumns.BUDGET_MAX, call.getBudgetMaxString());
        }
        if (StringUtil.isNotEmpty(call.getProjectNumber())) {
            document.addField(CallColumns.PROJECT_NUMBER, call.getProjectNumber());
        }
        if (call.getCreatedAt() != null) {
            document.addField(CallColumns.CREATED_AT, DateMapper.mapToSolrString(call.getCreatedAt()));
        } else {
            document.addField(CallColumns.CREATED_AT, DateMapper.mapToSolrString(LocalDateTime.now()));
        }
        return document;
    }

    private static LocalDateTime getDateInUTC(SolrDocument solrDocument, String dateString) {
        Object value = solrDocument.getFieldValue(dateString);
        if (value == null) {
            return null;
        }
        return DateMapper.toUTC((Date) value);
    }

    private static Short getProjectNumber(SolrDocument solrDocument) {
        return Optional.ofNullable(solrDocument.getFieldValue(CallColumns.PROJECT_NUMBER))
                .map(Integer.class::cast)
                .map(Integer::shortValue)
                .orElse(null);
    }

    static List<CallDTO> mapToCall(List<SolrDocument> solrDocuments) {
        if (solrDocuments == null) {
            return null;
        }
        return solrDocuments.stream()
                .map(SolrMapper::mapToCall)
                .toList();
    }

    static CallDTO mapToCall(SolrDocument solrDocument) {
        Object scoreField = solrDocument.getFieldValue(CallColumns.SCORE);
        return CallDTO.builder()
                .id((Long) solrDocument.getFieldValue(CallColumns.ID))
                .identifier((String) solrDocument.getFieldValue(CallColumns.IDENTIFIER))
                .title((String) solrDocument.getFieldValue(CallColumns.TITLE))
                .actionType(valueOrDefault((String) solrDocument.getFieldValue(CallColumns.ACTION_TYPE), null))
                .endDate(getDateInUTC(solrDocument, CallColumns.END_DATE))
                .endDate2(getDateInUTC(solrDocument, CallColumns.END_DATE_2))
                .startDate(getDateInUTC(solrDocument, CallColumns.START_DATE))
                .budgetMin(NumberMapper.shortenNumber(valueOrDefault((String) solrDocument.getFieldValue(CallColumns.BUDGET_MIN), (String) solrDocument.getFieldValue(CallColumns.BUDGET_MAX))))
                .budgetMax(NumberMapper.shortenNumber(valueOrDefault((String) solrDocument.getFieldValue(CallColumns.BUDGET_MAX), (String) solrDocument.getFieldValue(CallColumns.BUDGET_MIN))))
                .projectNumber(getProjectNumber(solrDocument))
                .score(scoreField != null ? (Float) scoreField : 0)
                .build(
                );
    }
    static List<SolrInputDocument> mapToSolrDocument(List<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream()
                .map(SolrMapper::mapToSolrDocument)
                .toList();
    }

    static SolrInputDocument mapToSolrDocument(Project project) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ProjectColumns.ID, project.getId());
        if (StringUtil.isNotEmpty(project.getAcronym())) {
            document.addField(ProjectColumns.ACRONYM, project.getAcronym());
        }
        if (StringUtil.isNotEmpty(project.getTitle())) {
            document.addField(ProjectColumns.TITLE, project.getTitle());
        }
        if (project.getEndDate() != null) {
            document.addField(ProjectColumns.END_DATE, DateMapper.mapToSolrString(project.getEndDate()));
        }
        if (project.getStartDate() != null) {
            document.addField(ProjectColumns.START_DATE, DateMapper.mapToSolrString(project.getStartDate()));
        }
        if (project.getCall() != null) {
            document.addField(ProjectColumns.CALL_ID, project.getCall().getId());
            if (StringUtil.isNotEmpty(project.getCall().getIdentifier())) {
                document.addField(ProjectColumns.CALL_IDENTIFIER, project.getCall().getIdentifier());
            }
        }
        if (StringUtil.isNotEmpty(project.getFundingEU())) {
            document.addField(ProjectColumns.FUNDING_EU, project.getFundingEUString());
        }
        if (StringUtil.isNotEmpty(project.getLongTextsToString())) {
            document.addField(ProjectColumns.LONG_TEXT, project.getLongTextsToString());
        }
        return document;
    }

    static ProjectDTO mapToProject(SolrDocument solrDocument) {
        return ProjectDTO.builder()
                .id((Long) solrDocument.getFieldValue(ProjectColumns.ID))
                .title((String) solrDocument.getFieldValue(ProjectColumns.TITLE))
                .acronym((String) solrDocument.getFieldValue(ProjectColumns.ACRONYM))
                .endDate(getDateInUTC(solrDocument, CallColumns.END_DATE))
                .startDate(getDateInUTC(solrDocument, CallColumns.START_DATE))
                .callId((Long) solrDocument.getFieldValue(ProjectColumns.CALL_ID))
                .fundingEU(NumberMapper.shortenNumber((String) solrDocument.getFieldValue(ProjectColumns.FUNDING_EU)))
                .score((Float) solrDocument.getFieldValue(ProjectColumns.SCORE))
                .build();
    }

    static List<ProjectDTO> mapToProject(List<SolrDocument> solrDocuments) {
        if (solrDocuments == null) {
            return null;
        }
        return solrDocuments.stream()
                .map(SolrMapper::mapToProject)
                .toList();
    }

}
