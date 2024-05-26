package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.enums.ActionTypeEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
public class DataLoader {

    @Autowired
    CallRepository callRepository;

    @Test
    void loadData() {
        String excelFilePath = "data/MasterExcelSheet.xlsx";

        try (FileInputStream fis = new FileInputStream(new ClassPathResource(excelFilePath).getFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            // get headers
            int identifierIndex = 0;
            int titleIndex = 1;
            int submissionDLIndex = 2;
            int actionTypeIndex = 3;
            int openDateIndex = 4;
            int submissionDL2Index = 5;
            int budgetIndex = 6;
            int descriptionIndex = 7;

            var headers = sheet.getRow(0);
            for (Cell cell : headers) {
                switch (cell.getStringCellValue().toLowerCase().replace(" ", "_")) {
                    case "identifier" -> identifierIndex = cell.getColumnIndex();
                    case "title" -> titleIndex = cell.getColumnIndex();
                    case "submission_dl" -> submissionDLIndex = cell.getColumnIndex();
                    case "action_type" -> actionTypeIndex = cell.getColumnIndex();
                    case "date_open" -> openDateIndex = cell.getColumnIndex();
                    case "submission_dl2" -> submissionDL2Index = cell.getColumnIndex();
                    case "budget" -> budgetIndex = cell.getColumnIndex();
                    case "topic_discription" -> descriptionIndex = cell.getColumnIndex();
                }
            }

            for (Row row : sheet) {
                System.out.println("Row: " + row.getRowNum());
                if (row.getRowNum() == 0) {
                    continue; // skip headers
                } else if (row.getCell(identifierIndex) == null || row.getCell(identifierIndex).getStringCellValue().isEmpty()) {
                    continue; // stop when identifier is empty
                }

                ActionTypeEnum actionType = getActionType(actionTypeIndex, row);

                String budgetFullString = getBudget(budgetIndex, row);
                String budget;
                String numberOfProjects;

                if (budgetFullString.contains("-")) {
                    budget = budgetFullString.split("-")[0].trim();
                    numberOfProjects = budgetFullString.split("-")[1].trim();
                } else {
                    budget = budgetFullString;
                    numberOfProjects = "1";
                }

                Timestamp submissionDL = getDate(submissionDLIndex, row);
                Timestamp submissionDL2 = getDate(submissionDL2Index, row);
                Timestamp openDate = getDate(openDateIndex, row);

                Call call = Call.builder()
                        .identifier(row.getCell(identifierIndex).getStringCellValue())
                        .title(row.getCell(titleIndex).getStringCellValue())
                        .submissionDeadlineDate(submissionDL)
                        .actionType(actionType)
                        .openDate(openDate)
                        .submissionDeadline2Date(submissionDL2)
                        .budget(budget)
                        .description(row.getCell(descriptionIndex).getStringCellValue())
                        .projectNumber(Short.parseShort(numberOfProjects))
                        .build();
                Optional<Call> existingCall = callRepository.findByIdentifier(call.getIdentifier());
                if (existingCall.isPresent() && call.getIdentifier().equals(existingCall.get().getIdentifier())) {
                    Call callToSave = existingCall.get();
                    callToSave.setTitle(call.getTitle());
                    callToSave.setSubmissionDeadlineDate(call.getSubmissionDeadlineDate());
                    callToSave.setActionType(call.getActionType());
                    callToSave.setOpenDate(call.getOpenDate());
                    callToSave.setSubmissionDeadline2Date(call.getSubmissionDeadline2Date());
                    callToSave.setBudget(call.getBudget());
                    callToSave.setDescription(call.getDescription());
                    callToSave.setProjectNumber(call.getProjectNumber());
                    callRepository.save(callToSave);
                } else {
                    callRepository.save(call);
                }

            }
        } catch (IOException | DataIntegrityViolationException e) {
            e.printStackTrace();
        }
    }

    private static ActionTypeEnum getActionType(int actionTypeIndex, Row row) {
        ActionTypeEnum actionType = null;
        if (row.getCell(actionTypeIndex) != null) {
            actionType = ActionTypeEnum.of(row.getCell(actionTypeIndex).getStringCellValue());
        }
        return actionType;
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
        return DateMapper.map(row.getCell(submissionDLIndex) != null ? row.getCell(submissionDLIndex).getLocalDateTimeCellValue() : null);
    }

}
