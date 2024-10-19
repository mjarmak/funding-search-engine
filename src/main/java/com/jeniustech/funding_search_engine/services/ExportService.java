package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.CallMapper;
import com.jeniustech.funding_search_engine.mappers.NumberMapper;
import com.jeniustech.funding_search_engine.mappers.PartnerMapper;
import com.jeniustech.funding_search_engine.mappers.ProjectMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.OrganisationRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
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

    private static final int WIDTH_MEDIUM = 256 * 20;
    XSSFColor PRIMARY_COLOR = new XSSFColor(new byte[]{(byte) 78, (byte) 85, (byte) 243}, null);

    private final CallRepository callRepository;
    private final ProjectRepository projectRepository;
    private final OrganisationRepository partnerRepository;
    private final UserDataRepository userDataRepository;
    private final LogService logService;

    public ByteArrayInputStream generateCallExcel(List<Long> callIds, String subjectId, String timezone) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        List<Call> calls = callRepository.findAllById(callIds);
        CallMapper.sortByEndDate(calls);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("CALLS");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Call Identifier");
            headerRow.createCell(1).setCellValue("Topic");
            headerRow.createCell(2).setCellValue("Open Date (" + timezone + ")");
            headerRow.createCell(3).setCellValue("Submission Deadline 1 (" + timezone + ")");
            headerRow.createCell(4).setCellValue("Submission Deadline 2 (" + timezone + ")");
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
                dataRow.createCell(2).setCellValue(StringUtil.valueOrDefault(call.getStartDateDisplay(timezone), ""));
                dataRow.createCell(3).setCellValue(StringUtil.valueOrDefault(call.getEndDateDisplay(timezone), ""));
                dataRow.createCell(4).setCellValue(StringUtil.valueOrDefault(call.getEndDate2Display(timezone), ""));
                dataRow.createCell(5).setCellValue(call.getActionType());
                dataRow.createCell(6).setCellValue(call.getBudgetRangeString());
                dataRow.createCell(7).setCellValue(StringUtil.valueOrDefault(call.getTypeOfMGADescription(), ""));
                dataRow.createCell(8).setCellValue(StringUtil.valueOrDefault(call.getUrl(), ""));
                dataRow.createCell(9).setCellValue(StringUtil.valueOrDefault(call.getInnovilyseUrl(), ""));
            }

            sheet.setColumnWidth(0, WIDTH_MEDIUM);
            sheet.setColumnWidth(1, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(2, WIDTH_MEDIUM);
            sheet.setColumnWidth(3, WIDTH_MEDIUM);
            sheet.setColumnWidth(4, WIDTH_MEDIUM);
            sheet.setColumnWidth(5, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(6, WIDTH_MEDIUM);
            sheet.setColumnWidth(7, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(8, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(9, WIDTH_MEDIUM * 2);

            setHeaderStyle(workbook, sheet, headerRow);

            workbook.write(out);
            logService.addLog(userData, EXPORT_EXCEL, calls.size() + "call");
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generateProjectExcel(List<Long> ids, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        List<Project> items = projectRepository.findAllById(ids);
        ProjectMapper.sortByEndDate(items);

        return generateProjectExcel(userData, items);
    }

    public ByteArrayInputStream generatePartnerProjectExcel(Long id, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        Organisation organisation = partnerRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Organisation not found"));

        return generateProjectExcel(userData, organisation.getProjects());
    }

    public ByteArrayInputStream generateCallProjectExcel(Long id, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        Call call = callRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Call not found"));

        return generateProjectExcel(userData, call.getProjects());
    }

    private ByteArrayInputStream generateProjectExcel(UserData userData, List<Project> items) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("PROJECTS");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Acronym");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Call Identifier");
            headerRow.createCell(3).setCellValue("Call Budget (EUR)");
            headerRow.createCell(4).setCellValue("Start Date");
            headerRow.createCell(5).setCellValue("End Date");
            headerRow.createCell(6).setCellValue("Sign Date");
            headerRow.createCell(7).setCellValue("Funding EU (EUR)");
            headerRow.createCell(8).setCellValue("Funding Organisation (EUR)");
            headerRow.createCell(9).setCellValue("Status");
            headerRow.createCell(10).setCellValue("Framework Program");
            headerRow.createCell(11).setCellValue("Funding Scheme");
            headerRow.createCell(12).setCellValue("Legal Basis");
            headerRow.createCell(13).setCellValue("EU Portal URL");
            headerRow.createCell(14).setCellValue("URL");

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
                dataRow.createCell(10).setCellValue(StringUtil.valueOrDefault(project.getFrameworkProgramName(), ""));
                dataRow.createCell(11).setCellValue(StringUtil.valueOrDefault(project.getFundingSchemeName(), ""));
                dataRow.createCell(12).setCellValue(StringUtil.valueOrDefault(project.getLegalBasis(), ""));
                dataRow.createCell(13).setCellValue(StringUtil.valueOrDefault(project.getUrl(), ""));
                dataRow.createCell(14).setCellValue(StringUtil.valueOrDefault(project.getInnovilyseUrl(), ""));
            }

            sheet.setColumnWidth(0, WIDTH_MEDIUM);
            sheet.setColumnWidth(1, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(2, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(3, WIDTH_MEDIUM);
            sheet.setColumnWidth(4, WIDTH_MEDIUM);
            sheet.setColumnWidth(5, WIDTH_MEDIUM);
            sheet.setColumnWidth(6, WIDTH_MEDIUM);
            sheet.setColumnWidth(7, WIDTH_MEDIUM);
            sheet.setColumnWidth(8, WIDTH_MEDIUM);
            sheet.setColumnWidth(10, WIDTH_MEDIUM);
            sheet.setColumnWidth(11, WIDTH_MEDIUM);
            sheet.setColumnWidth(12, WIDTH_MEDIUM);
            sheet.setColumnWidth(13, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(14, WIDTH_MEDIUM * 2);

            setHeaderStyle(workbook, sheet, headerRow);

            workbook.write(out);
            logService.addLog(userData, EXPORT_EXCEL, items.size() + "project");
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generatePartnerExcel(List<Long> ids, String subjectId) throws IOException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserExcelExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_EXCEL));

        List<Organisation> partners = partnerRepository.findAllById(ids);
        PartnerMapper.sortByName(partners);

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
                dataRow.createCell(0, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getName(), ""));
                dataRow.createCell(1, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getShortName(), ""));
                dataRow.createCell(2, CellType.NUMERIC).setCellValue(StringUtil.valueOrDefault(NumberMapper.formatNumberWithCommas(partner.getFundingEU()), ""));
                dataRow.createCell(3, CellType.NUMERIC).setCellValue(StringUtil.valueOrDefault(NumberMapper.formatNumberWithCommas(partner.getFundingOrganisation()), ""));
                dataRow.createCell(4, CellType.NUMERIC).setCellValue(StringUtil.valueOrDefault(partner.getProjectNumber(), ""));
                dataRow.createCell(5, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getCountryCode(), ""));
                dataRow.createCell(6, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getTypeName(), ""));
                dataRow.createCell(7, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getAddressString(), ""));
                dataRow.createCell(8, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getVatNumber(), ""));
                dataRow.createCell(9, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getNutsCode(), ""));
                dataRow.createCell(10, CellType.STRING).setCellValue(partner.isSme());
                dataRow.createCell(11, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getWebSiteUrl(), ""));
                dataRow.createCell(12, CellType.STRING).setCellValue(StringUtil.valueOrDefault(partner.getInnovilyseUrl(), ""));
            }

            sheet.setColumnWidth(0, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(1, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(2, WIDTH_MEDIUM);
            sheet.setColumnWidth(3, WIDTH_MEDIUM);
            sheet.setColumnWidth(5, WIDTH_MEDIUM / 2);
            sheet.setColumnWidth(6, WIDTH_MEDIUM);
            sheet.setColumnWidth(7, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(8, WIDTH_MEDIUM);
            sheet.setColumnWidth(11, WIDTH_MEDIUM * 2);
            sheet.setColumnWidth(12, WIDTH_MEDIUM * 2);

            setHeaderStyle(workbook, sheet, headerRow);

            workbook.write(out);
            logService.addLog(userData, EXPORT_EXCEL, partners.size() + "partner");
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void setHeaderStyle(Workbook workbook, Sheet sheet, Row headerRow) {
        final int numberOfCells = headerRow.getPhysicalNumberOfCells();
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, numberOfCells - 1));

        sheet.createFreezePane(0, 1);
        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillForegroundColor(PRIMARY_COLOR);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        font.setBold(true);

        headerStyle.setFont(font);

        for (int i = 0; i < numberOfCells; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
    }

}
