package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.NumberMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.OrganisationRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
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

import static com.jeniustech.funding_search_engine.enums.LogTypeEnum.EXPORT_EXCEL;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final CallRepository callRepository;
    private final ProjectRepository projectRepository;
    private final OrganisationRepository partnerRepository;
    private final UserDataRepository userDataRepository;
    private final LogService logService;

    public ByteArrayInputStream generateCallExcel(List<Long> callIds, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        List<Call> calls = callRepository.findAllById(callIds);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("CALLS");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Call Identifier");
            headerRow.createCell(1).setCellValue("Topic");
            headerRow.createCell(2).setCellValue("Open Date (UTC)");
            headerRow.createCell(3).setCellValue("Submission Deadline 1 (UTC)");
            headerRow.createCell(4).setCellValue("Submission Deadline 2 (UTC)");
            headerRow.createCell(5).setCellValue("Action Type");
            headerRow.createCell(6).setCellValue("Budget (EUR)");
            headerRow.createCell(7).setCellValue("Type Of MGA");
            headerRow.createCell(8).setCellValue("EU Portal URL");
            headerRow.createCell(9).setCellValue("URL");

            for (int i = 0; i < calls.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                Call call = calls.get(i);
                dataRow.createCell(0).setCellValue(StringUtil.valueOrDefault(call.getIdentifier(), ""));
                dataRow.createCell(1).setCellValue(StringUtil.valueOrDefault(call.getTitle(), ""));
                dataRow.createCell(2).setCellValue(StringUtil.valueOrDefault(call.getStartDateDisplay(), ""));
                dataRow.createCell(3).setCellValue(StringUtil.valueOrDefault(call.getEndDateDisplay(), ""));
                dataRow.createCell(4).setCellValue(StringUtil.valueOrDefault(call.getEndDate2Display(), ""));
                dataRow.createCell(5).setCellValue(call.getActionType());
                dataRow.createCell(6).setCellValue(call.getBudgetRangeString());
                dataRow.createCell(7).setCellValue(StringUtil.valueOrDefault(call.getTypeOfMGADescription(), ""));
                dataRow.createCell(8).setCellValue(StringUtil.valueOrDefault(call.getUrl(), ""));
                dataRow.createCell(9).setCellValue(StringUtil.valueOrDefault(call.getInnovilyseUrl(), ""));
            }

            workbook.write(out);
            logService.addLog(userData, EXPORT_EXCEL, calls.size() + "call");
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generateProjectExcel(List<Long> ids, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        List<Project> items = projectRepository.findAllById(ids);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("PROJECTS");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Acronym");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Call Identifier");
            headerRow.createCell(3).setCellValue("Call Budget (EUR)");
            headerRow.createCell(4).setCellValue("Start Date (UTC)");
            headerRow.createCell(5).setCellValue("End Date (UTC)");
            headerRow.createCell(6).setCellValue("Sign Date (UTC)");
            headerRow.createCell(7).setCellValue("Funding EU (EUR)");
            headerRow.createCell(8).setCellValue("Funding Organisation (EUR)");
            headerRow.createCell(9).setCellValue("Status");
            headerRow.createCell(10).setCellValue("Funding Scheme");
            headerRow.createCell(11).setCellValue("Legal Basis");
            headerRow.createCell(12).setCellValue("EU Portal URL");
            headerRow.createCell(13).setCellValue("URL");

            for (int i = 0; i < items.size(); i++) {
                Project project = items.get(i);
                Call call = project.getCall();
                String callIdentifier = call != null ? call.getIdentifier() : null;
                String budgetRange = call != null ? call.getBudgetRangeString() : null;

                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(StringUtil.valueOrDefault(project.getAcronym(), ""));
                dataRow.createCell(1).setCellValue(StringUtil.valueOrDefault(project.getTitle(), ""));
                dataRow.createCell(2).setCellValue(StringUtil.valueOrDefault(callIdentifier, ""));
                dataRow.createCell(3).setCellValue(budgetRange);
                dataRow.createCell(4).setCellValue(StringUtil.valueOrDefault(project.getStartDateDisplay(), ""));
                dataRow.createCell(5).setCellValue(StringUtil.valueOrDefault(project.getEndDateDisplay(), ""));
                dataRow.createCell(6).setCellValue(StringUtil.valueOrDefault(project.getSignDateDisplay(), ""));
                dataRow.createCell(7).setCellValue(StringUtil.valueOrDefault(NumberMapper.formatNumberWithCommas(project.getFundingEU()), ""));
                dataRow.createCell(8).setCellValue(StringUtil.valueOrDefault(NumberMapper.formatNumberWithCommas(project.getFundingOrganisation()), ""));
                dataRow.createCell(9).setCellValue(StringUtil.valueOrDefault(project.getStatusName(), ""));
                dataRow.createCell(10).setCellValue(StringUtil.valueOrDefault(project.getFundingSchemeName(), ""));
                dataRow.createCell(11).setCellValue(StringUtil.valueOrDefault(project.getLegalBasis(), ""));
                dataRow.createCell(12).setCellValue(StringUtil.valueOrDefault(project.getUrl(), ""));
                dataRow.createCell(13).setCellValue(StringUtil.valueOrDefault(project.getInnovilyseUrl(), ""));
            }

            workbook.write(out);
            logService.addLog(userData, EXPORT_EXCEL, items.size() + "project");
            return new ByteArrayInputStream(out.toByteArray());
        }

    }

    public ByteArrayInputStream generatePartnerExcel(List<Long> ids, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        List<Organisation> partners = partnerRepository.findAllById(ids);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("PARTNERS");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Short Name");
            headerRow.createCell(2).setCellValue("Funding EU (EUR)");
            headerRow.createCell(3).setCellValue("Funding Organisation (EUR)");
            headerRow.createCell(4).setCellValue("Number of Project");
            headerRow.createCell(5).setCellValue("Country Code");
            headerRow.createCell(6).setCellValue("Entity Type");
            headerRow.createCell(7).setCellValue("Address");
            headerRow.createCell(8).setCellValue("VAT Number");
            headerRow.createCell(9).setCellValue("NUTS Code");
            headerRow.createCell(10).setCellValue("SME");
            headerRow.createCell(11).setCellValue("Web Site URL");
            headerRow.createCell(12).setCellValue("URL");

            for (int i = 0; i < partners.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                Organisation partner = partners.get(i);
                dataRow.createCell(0).setCellValue(StringUtil.valueOrDefault(partner.getName(), ""));
                dataRow.createCell(1).setCellValue(StringUtil.valueOrDefault(partner.getShortName(), ""));
                dataRow.createCell(2).setCellValue(StringUtil.valueOrDefault(NumberMapper.formatNumberWithCommas(partner.getFundingEU()), ""));
                dataRow.createCell(3).setCellValue(StringUtil.valueOrDefault(NumberMapper.formatNumberWithCommas(partner.getFundingOrganisation()), ""));
                dataRow.createCell(4).setCellValue(partner.getProjectNumber());
                dataRow.createCell(5).setCellValue(StringUtil.valueOrDefault(partner.getCountryCode(), ""));
                dataRow.createCell(6).setCellValue(StringUtil.valueOrDefault(partner.getTypeName(), ""));
                dataRow.createCell(7).setCellValue(StringUtil.valueOrDefault(partner.getAddressString(), ""));
                dataRow.createCell(8).setCellValue(StringUtil.valueOrDefault(partner.getVatNumber(), ""));
                dataRow.createCell(9).setCellValue(StringUtil.valueOrDefault(partner.getNutsCode(), ""));
                dataRow.createCell(10).setCellValue(partner.isSme());
                dataRow.createCell(11).setCellValue(StringUtil.valueOrDefault(partner.getWebSiteUrl(), ""));
                dataRow.createCell(12).setCellValue(StringUtil.valueOrDefault(partner.getInnovilyseUrl(), ""));
            }

            workbook.write(out);
            logService.addLog(userData, EXPORT_EXCEL, partners.size() + "partner");
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
