package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final CallRepository callRepository;

    public ByteArrayInputStream generateExcel(List<Long> callIds) throws IOException {

        List<Call> calls = callRepository.findAllById(callIds);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Data");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Call Identifier");
            headerRow.createCell(1).setCellValue("Topic");
            headerRow.createCell(2).setCellValue("Open Date");
            headerRow.createCell(3).setCellValue("Submission Deadline 1");
            headerRow.createCell(4).setCellValue("Submission Deadline 2");
            headerRow.createCell(5).setCellValue("Action Type");
            headerRow.createCell(6).setCellValue("Budget (EUR)");
            headerRow.createCell(7).setCellValue("Type Of MGA");
            headerRow.createCell(8).setCellValue("URL");

            for (int i = 0; i < calls.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(StringUtil.valueOrDefault(calls.get(i).getIdentifier(), ""));
                dataRow.createCell(1).setCellValue(StringUtil.valueOrDefault(calls.get(i).getTitle(), ""));
                dataRow.createCell(2).setCellValue(StringUtil.valueOrDefault(calls.get(i).getStartDateDisplay(), ""));
                dataRow.createCell(3).setCellValue(StringUtil.valueOrDefault(calls.get(i).getEndDateDisplay(), ""));
                dataRow.createCell(4).setCellValue(StringUtil.valueOrDefault(calls.get(i).getEndDate2Display(), ""));
                dataRow.createCell(5).setCellValue(calls.get(i).getActionType());
                dataRow.createCell(6).setCellValue(calls.get(i).getBudgetRangeString());
                dataRow.createCell(7).setCellValue(StringUtil.valueOrDefault(calls.get(i).getTypeOfMGADescription(), ""));
                dataRow.createCell(8).setCellValue(StringUtil.valueOrDefault(calls.get(i).getUrl(), ""));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

}
