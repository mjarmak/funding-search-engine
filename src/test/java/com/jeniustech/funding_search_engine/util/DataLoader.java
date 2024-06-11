package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.services.SolrClientService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class DataLoader {

    public static final String IDENTIFIER = "identifier";
    public static final String TITLE = "title";
    public static final String ACTION_TYPE = "action_type";
    public static final String TYPE_OF_MGA = "type_of_mga";
    public static final String TYPE_OF_MGA_DESCRIPTION = "type_of_mga_description";
    public static final String SUBMISSION_DL = "deadline";
    public static final String SUBMISSION_DL2 = "deadline2";
    public static final String SUBMISSION_PROCEDURE = "submission_procedure";
    public static final String DATE_OPEN = "start_date";
    public static final String BUDGET_MIN = "budget_min";
    public static final String BUDGET_MAX = "budget_max";
    public static final String NUMBER_OF_PROJECTS = "project_number";
    public static final String PATH_ID = "path_id";
    public static final String REFERENCE = "reference";
    public static final String DESCRIPTION = "description";
    public static final String MISSION_DETAILS = "mission_details";
    public static final String DESTINATION_DETAILS = "destination_details";

    @Autowired CallRepository callRepository;
    @Autowired SolrClientService solrClientService;

    @Test
    void loadData() {
        String excelFilePath = "data/MasterExcelSheet.xlsx";

        try (FileInputStream fis = new FileInputStream(new ClassPathResource(excelFilePath).getFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

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
            int typeOfMGAIndex = 0;
            int typeOfMGADescriptionIndex = 0;
            int submissionProcedureIndex = 0;

            var headers = sheet.getRow(0);
            for (Cell cell : headers) {
                switch (cell.getStringCellValue().toLowerCase().replace(" ", "_")) {
                    case IDENTIFIER -> identifierIndex = cell.getColumnIndex();
                    case TITLE -> titleIndex = cell.getColumnIndex();
                    case SUBMISSION_DL -> submissionDLIndex = cell.getColumnIndex();
                    case SUBMISSION_DL2 -> submissionDL2Index = cell.getColumnIndex();
                    case ACTION_TYPE -> actionTypeIndex = cell.getColumnIndex();
                    case DATE_OPEN -> openDateIndex = cell.getColumnIndex();
                    case BUDGET_MIN -> budgetMinIndex = cell.getColumnIndex();
                    case BUDGET_MAX -> budgetMaxIndex = cell.getColumnIndex();
                    case DESCRIPTION -> descriptionIndex = cell.getColumnIndex();
                    case NUMBER_OF_PROJECTS -> numberOfProjectsIndex = (int) cell.getNumericCellValue();
                    case PATH_ID -> pathIdIndex = cell.getColumnIndex();
                    case REFERENCE -> referenceIndex = cell.getColumnIndex();
                    case DESTINATION_DETAILS -> destinationDetailsIndex = cell.getColumnIndex();
                    case MISSION_DETAILS -> missionDetailsIndex = cell.getColumnIndex();
                    case TYPE_OF_MGA -> typeOfMGAIndex = cell.getColumnIndex();
                    case TYPE_OF_MGA_DESCRIPTION -> typeOfMGADescriptionIndex = cell.getColumnIndex();
                    case SUBMISSION_PROCEDURE -> submissionProcedureIndex = cell.getColumnIndex();
                }
            }

            for (Row row : sheet) {
                System.out.println("Row: " + row.getRowNum());
                if (row.getRowNum() == 0) {
                    continue; // skip headers
                } else if (row.getCell(identifierIndex) == null || row.getCell(identifierIndex).getStringCellValue().isEmpty()) {
                    System.out.println("Identifier is empty for row: " + row.getRowNum());
                    continue; // stop when identifier is empty
                }

//                if (actionType == null) {
//                    System.out.println("Action type is null for row: " + row.getRowNum());
//                    continue; // stop when action type is null
//                }

                Call call = Call.builder()
                        .identifier(row.getCell(identifierIndex).getStringCellValue())
                        .title(row.getCell(titleIndex).getStringCellValue())
                        .description(row.getCell(descriptionIndex).getStringCellValue())
                        .destinationDetails(row.getCell(destinationDetailsIndex).getStringCellValue())
                        .missionDetails(row.getCell(missionDetailsIndex).getStringCellValue())
                        .submissionProcedure(SubmissionProcedureEnum.of(row.getCell(submissionProcedureIndex).getStringCellValue()))
                        .submissionDeadlineDate(getDate(submissionDLIndex, row))
                        .submissionDeadlineDate2(getDate(submissionDL2Index, row))
                        .actionType(row.getCell(actionTypeIndex).getStringCellValue())
                        .openDate(getDate(openDateIndex, row))
                        .budgetMin(BigDecimal.valueOf(row.getCell(budgetMinIndex).getNumericCellValue()))
                        .budgetMax(BigDecimal.valueOf(row.getCell(budgetMaxIndex).getNumericCellValue()))
                        .projectNumber(Short.parseShort(row.getCell(numberOfProjectsIndex).getStringCellValue()))
                        .pathId(row.getCell(pathIdIndex).getStringCellValue())
                        .reference(row.getCell(referenceIndex).getStringCellValue())
                        .typeOfMGA(row.getCell(typeOfMGAIndex).getStringCellValue())
                        .typeOfMGADescription(row.getCell(typeOfMGADescriptionIndex).getStringCellValue())
                        .build();
                call.setDisplayDescription(call.getDisplayDescription());
                Optional<Call> existingCall = callRepository.findByIdentifier(call.getIdentifier());
                if (existingCall.isPresent() && call.getIdentifier().equals(existingCall.get().getIdentifier())) {
                    Call callToSave = existingCall.get();
                    callToSave.setIdentifier(call.getIdentifier());
                    callToSave.setTitle(call.getTitle());
                    callToSave.setDescription(call.getDescription());
                    callToSave.setDisplayDescription(call.getDisplayDescription());
                    callToSave.setSubmissionDeadlineDate(call.getSubmissionDeadlineDate());
                    callToSave.setActionType(call.getActionType());
                    callToSave.setOpenDate(call.getOpenDate());
                    callToSave.setBudgetMin(call.getBudgetMin());
                    callToSave.setBudgetMax(call.getBudgetMax());
                    callToSave.setProjectNumber(call.getProjectNumber());
                    callToSave.setPathId(call.getPathId());
                    callToSave.setReference(call.getReference());
                    callToSave.setSubmissionProcedure(call.getSubmissionProcedure());
                    callToSave.setDestinationDetails(call.getDestinationDetails());
                    callToSave.setMissionDetails(call.getMissionDetails());
                    callToSave.setTypeOfMGA(call.getTypeOfMGA());
                    callToSave.setTypeOfMGADescription(call.getTypeOfMGADescription());

                    callRepository.save(callToSave);
                    solrClientService.add(SolrMapper.map(callToSave), 100_000);
                } else {
                    Call savedCall = callRepository.save(call);
                    solrClientService.add(SolrMapper.map(savedCall), 100_000);
                }

            }
        } catch (IOException | DataIntegrityViolationException e) {
            e.printStackTrace();
            fail();
        }
    }

    private static String getActionType(int actionTypeIndex, Row row) {
        if (row.getCell(actionTypeIndex) != null) {
            return row.getCell(actionTypeIndex).getStringCellValue();
        }
        return null;
    }

    private static String getBudget(int budgetIndex, Row row) {
        String budget = null;
        Cell budgetCell = row.getCell(budgetIndex);
        if (budgetCell.getCellType() == CellType.NUMERIC) {
            budget = String.valueOf(budgetCell.getNumericCellValue());
        } else {
            budget = budgetCell.getStringCellValue();
        }
        return budget;
    }

    private static Timestamp getDate(int submissionDLIndex, Row row) {
        if (row.getCell(submissionDLIndex) == null || row.getCell(submissionDLIndex).getLocalDateTimeCellValue() == null) {
            return null;
        }
        return DateMapper.map(row.getCell(submissionDLIndex).getLocalDateTimeCellValue());
    }

}
