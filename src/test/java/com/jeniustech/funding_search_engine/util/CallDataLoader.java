package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.services.solr.CallSolrClientService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.constants.excel.CallColumns.*;
import static com.jeniustech.funding_search_engine.util.StringUtil.isNotEmpty;
import static com.jeniustech.funding_search_engine.util.StringUtil.valueOrDefault;
import static org.junit.jupiter.api.Assertions.fail;

@Transactional(rollbackFor = Exception.class)
@Rollback(false)
@SpringBootTest
public class CallDataLoader {

    @Autowired
    CallRepository callRepository;
    @Autowired
    CallSolrClientService callSolrClientService;

    @Test
    void loadData() {
        String path = "C:/Projects/funding-search-engine/src/test/resources/data/calls/";
        List<String> csvFiles = List.of(
                "output_query-open_1718653202242.csv",
                "output_query-upcoming_1718653130808.csv",
                "output_query-closed_1718653220506.csv"
        );
        for (String csvFile : csvFiles) {
            loadFile(path, csvFile);
        }
    }

    private void loadFile(String path, String csvFile) {
        try (CSVReader reader = new CSVReader(new FileReader(path + csvFile))) {

            // get headers
            int identifierIndex = 0;
            int titleIndex = 0;
            int submissionDLIndex = 0;
            int submissionDL2Index = 0;
            int actionTypeIndex = 0;
            int openDateIndex = 0;
            int budgetMinIndex = 0;
            int budgetMaxIndex = 0;
            int descriptionIndex = 0;
            int destinationDetailsIndex = 0;
            int missionDetailsIndex = 0;
            int numberOfProjectsIndex = 0;
            int pathIdIndex = 0;
            int referenceIndex = 0;
            int typeOfMGADescriptionIndex = 0;
            int submissionProcedureIndex = 0;
            int beneficiaryAdministrationIndex = 0;
            int durationIndex = 0;
            int furtherInformationIndex = 0;

            var headers = reader.readNext();
            int index = 0;
            for (String cell : headers) {
                switch (cell) {
                    case IDENTIFIER -> identifierIndex = index;
                    case TITLE -> titleIndex = index;
                    case SUBMISSION_DL -> submissionDLIndex = index;
                    case SUBMISSION_DL2 -> submissionDL2Index = index;
                    case ACTION_TYPE -> actionTypeIndex = index;
                    case DATE_OPEN -> openDateIndex = index;
                    case BUDGET_MIN -> budgetMinIndex = index;
                    case BUDGET_MAX -> budgetMaxIndex = index;
                    case DESCRIPTION -> descriptionIndex = index;
                    case NUMBER_OF_PROJECTS -> numberOfProjectsIndex = index;
                    case PATH_ID -> pathIdIndex = index;
                    case REFERENCE -> referenceIndex = index;
                    case DESTINATION_DETAILS -> destinationDetailsIndex = index;
                    case MISSION_DETAILS -> missionDetailsIndex = index;
                    case TYPE_OF_MGA_DESCRIPTION -> typeOfMGADescriptionIndex = index;
                    case SUBMISSION_PROCEDURE -> submissionProcedureIndex = index;
                    case BENEFICIARY_ADMINISTRATION -> beneficiaryAdministrationIndex = index;
                    case DURATION -> durationIndex = index;
                    case FURTHER_INFORMATION -> furtherInformationIndex = index;
                }
                index++;
            }

            if (titleIndex == 0 || submissionDLIndex == 0 || submissionDL2Index == 0 || actionTypeIndex == 0 || openDateIndex == 0 || budgetMinIndex == 0 || budgetMaxIndex == 0 || descriptionIndex == 0 || destinationDetailsIndex == 0 || missionDetailsIndex == 0 || numberOfProjectsIndex == 0 || pathIdIndex == 0 || referenceIndex == 0 || typeOfMGADescriptionIndex == 0 || submissionProcedureIndex == 0 || beneficiaryAdministrationIndex == 0 || durationIndex == 0 || furtherInformationIndex == 0) {
                System.out.println("Header not found");
                fail();
            }

            int rowNumber = 0;
            String[] row;
            while ((row = reader.readNext()) != null) {
                System.out.println("Row: " + rowNumber);
                if (row[identifierIndex] == null || row[identifierIndex].isEmpty()) {
                    System.out.println("Identifier is empty for row: " + rowNumber);
                    continue; // stop when identifier is empty
                }

                Call call = Call.builder()
                        .identifier(row[identifierIndex])
                        .reference(row[referenceIndex])
                        .title(row[titleIndex])
                        .actionType(valueOrDefault(row[actionTypeIndex], null))
                        .submissionProcedure(SubmissionProcedureEnum.of(row[submissionProcedureIndex]))
                        .endDate(DateMapper.mapToTimestamp(row[submissionDLIndex]))
                        .endDate2(DateMapper.mapToTimestamp(row[submissionDL2Index]))
                        .startDate(DateMapper.mapToTimestamp(row[openDateIndex]))
                        .budgetMin(new BigDecimal(row[budgetMinIndex]).stripTrailingZeros())
                        .budgetMax(new BigDecimal(row[budgetMaxIndex]).stripTrailingZeros())
                        .projectNumber(getProjectNumber(row[numberOfProjectsIndex]))
                        .typeOfMGADescription(valueOrDefault(row[typeOfMGADescriptionIndex], null))
                        .build();

                String reference = row[referenceIndex];
                String pathId = row[pathIdIndex];

                UrlTypeEnum urlType = UrlTypeEnum.getType(reference);
                call.setUrlType(urlType);

                if (urlType.equals(UrlTypeEnum.COMPETITIVE_CALL)) {
                    call.setUrlId(pathId);
                } else if (urlType.equals(UrlTypeEnum.PROSPECT)) {
                    call.setUrlId(reference);
                }

                call.setLongTexts(new ArrayList<>());

                addDescriptionIfPresent(row, descriptionIndex, call, LongTextTypeEnum.DESCRIPTION);
                addDescriptionIfPresent(row, missionDetailsIndex, call, LongTextTypeEnum.MISSION_DETAILS);
                addDescriptionIfPresent(row, destinationDetailsIndex, call, LongTextTypeEnum.DESTINATION_DETAILS);
                addDescriptionIfPresent(row, beneficiaryAdministrationIndex, call, LongTextTypeEnum.BENEFICIARY_ADMINISTRATION);
                addDescriptionIfPresent(row, durationIndex, call, LongTextTypeEnum.DURATION);
                addDescriptionIfPresent(row, furtherInformationIndex, call, LongTextTypeEnum.FURTHER_INFORMATION);

                for (LongText longText : call.getLongTexts()) {
                    longText.setCall(call);
                }

                try {
                    Optional<Call> existingCallOptional = callRepository.findByReference(call.getReference());
                    if (existingCallOptional.isPresent() && call.getIdentifier().equals(existingCallOptional.get().getIdentifier())) {
                        System.out.println("Updating call: " + call.getIdentifier());
                        Call existingCall = existingCallOptional.get();
                        for (LongText longText : call.getLongTexts()) {
                            if (existingCall.getLongTexts().stream().noneMatch(lt -> lt.getType().equals(longText.getType()))) {
                                longText.setCall(existingCall);
                                existingCall.getLongTexts().add(longText);
                            } else {
                                LongText longTextToSave = existingCall.getLongTexts().stream()
                                        .filter(lt -> lt.getType().equals(longText.getType()))
                                        .findFirst()
                                        .orElseThrow();
                                if (
                                        isNotEmpty(longTextToSave.getText())) {
                                    longTextToSave.setText(longText.getText());
                                }
                            }
                        }
                        if (isNotEmpty(call.getTitle())) {
                            existingCall.setTitle(call.getTitle());
                        }
                        if (isNotEmpty(call.getActionType())) {
                            existingCall.setActionType(call.getActionType());
                        }
                        if (isNotEmpty(call.getSubmissionProcedure())) {
                            existingCall.setSubmissionProcedure(call.getSubmissionProcedure());
                        }
                        if (isNotEmpty(call.getEndDate())) {
                            existingCall.setEndDate(call.getEndDate());
                        }
                        if (isNotEmpty(call.getEndDate2())) {
                            existingCall.setEndDate2(call.getEndDate2());
                        }
                        if (isNotEmpty(call.getStartDate())) {
                            existingCall.setStartDate(call.getStartDate());
                        }
                        if (isNotEmpty(call.getBudgetMin())) {
                            existingCall.setBudgetMin(call.getBudgetMin());
                        }
                        if (isNotEmpty(call.getBudgetMax())) {
                            existingCall.setBudgetMax(call.getBudgetMax());
                        }
                        if (isNotEmpty(call.getProjectNumber())) {
                            existingCall.setProjectNumber(call.getProjectNumber());
                        }
                        if (isNotEmpty(call.getUrlType())) {
                            existingCall.setUrlType(call.getUrlType());
                        }
                        if (isNotEmpty(call.getUrlId())) {
                            existingCall.setUrlId(call.getUrlId());
                        }
                        if (isNotEmpty(call.getTypeOfMGADescription())) {
                            existingCall.setTypeOfMGADescription(call.getTypeOfMGADescription());
                        }

                        callRepository.save(existingCall);
                        callSolrClientService.add(SolrMapper.mapToSolrDocument(existingCall), 100_000);
                    } else {
                        System.out.println("Adding call: " + call.getIdentifier());
                        Call savedCall = callRepository.save(call);
                        callSolrClientService.add(SolrMapper.mapToSolrDocument(savedCall), 100_000);
                    }
                } catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    fail();
                }
                rowNumber++;
            }
        } catch (IOException | ArrayIndexOutOfBoundsException | CsvValidationException e) {
            e.printStackTrace();
            fail();
        }
    }

    private static void addDescriptionIfPresent(String[] row, int descriptionIndex, Call call, LongTextTypeEnum description) {
        if (isNotEmpty(row[descriptionIndex])) {
            call.getLongTexts().add(LongText.builder().type(description).text(row[descriptionIndex]).build());
        }
    }

    private static Short getProjectNumber(String row) {
        if (!isNotEmpty(row)) {
            return null;
        }
        return Short.parseShort(row);
    }

}
