package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.constants.excel.ProjectColumns;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.services.solr.ProjectSolrClientService;
import jakarta.annotation.Nullable;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.util.StringUtil.isNotEmpty;
import static org.junit.jupiter.api.Assertions.fail;

@Transactional(rollbackFor = Exception.class)
@Rollback(false)
@SpringBootTest
public class ProjectDataLoader {

    @Autowired
    CallRepository callRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectSolrClientService projectSolrClientService;

    public static final int BATCH_SIZE = 1000;

    int ID_INDEX = -1;
    int ACRONYM_INDEX = -1;
    int STATUS_INDEX = -1;
    int TITLE_INDEX = -1;
    int START_DATE_INDEX = -1;
    int END_DATE_INDEX = -1;
    int TOTAL_COST_INDEX = -1;
    int EC_MAX_CONTRIBUTION_INDEX = -1;
    int LEGAL_BASIS_INDEX = -1;
    int CALL_IDENTIFIER_INDEX = -1;
    int EC_SIGNATURE_DATE_INDEX = -1;
    int MASTER_CALL_INDEX = -1;
    int FUNDING_SCHEME_INDEX = -1;
    int NATURE_INDEX = -1;
    int OBJECTIVE_INDEX = -1;
    int RCN_INDEX = -1;

    @Test
    void loadData() {
        String excelFilePath = "data/projects/project.xlsx";

        try (FileInputStream fis = new FileInputStream(new ClassPathResource(excelFilePath).getFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            // get headers
            var headers = sheet.getRow(0);
            for (Cell cell : headers) {
                switch (cell.getStringCellValue()) {
                    case ProjectColumns.ID -> ID_INDEX = cell.getColumnIndex();
                    case ProjectColumns.ACRONYM -> ACRONYM_INDEX = cell.getColumnIndex();
                    case ProjectColumns.STATUS -> STATUS_INDEX = cell.getColumnIndex();
                    case ProjectColumns.TITLE -> TITLE_INDEX = cell.getColumnIndex();
                    case ProjectColumns.START_DATE -> START_DATE_INDEX = cell.getColumnIndex();
                    case ProjectColumns.END_DATE -> END_DATE_INDEX = cell.getColumnIndex();
                    case ProjectColumns.TOTAL_COST -> TOTAL_COST_INDEX = cell.getColumnIndex();
                    case ProjectColumns.EC_MAX_CONTRIBUTION -> EC_MAX_CONTRIBUTION_INDEX = cell.getColumnIndex();
                    case ProjectColumns.LEGAL_BASIS -> LEGAL_BASIS_INDEX = cell.getColumnIndex();
                    case ProjectColumns.CALL_IDENTIFIER -> CALL_IDENTIFIER_INDEX = cell.getColumnIndex();
                    case ProjectColumns.EC_SIGNATURE_DATE -> EC_SIGNATURE_DATE_INDEX = cell.getColumnIndex();
                    case ProjectColumns.MASTER_CALL -> MASTER_CALL_INDEX = cell.getColumnIndex();
                    case ProjectColumns.FUNDING_SCHEME -> FUNDING_SCHEME_INDEX = cell.getColumnIndex();
                    case ProjectColumns.NATURE -> NATURE_INDEX = cell.getColumnIndex();
                    case ProjectColumns.OBJECTIVE -> OBJECTIVE_INDEX = cell.getColumnIndex();
                    case ProjectColumns.RCN -> RCN_INDEX = cell.getColumnIndex();
                }
            }

            if (
                    List.of(
                            ID_INDEX,
                            ACRONYM_INDEX,
                            STATUS_INDEX,
                            TITLE_INDEX,
                            START_DATE_INDEX,
                            END_DATE_INDEX,
                            TOTAL_COST_INDEX,
                            EC_MAX_CONTRIBUTION_INDEX,
                            LEGAL_BASIS_INDEX,
                            CALL_IDENTIFIER_INDEX,
                            EC_SIGNATURE_DATE_INDEX,
                            MASTER_CALL_INDEX,
                            FUNDING_SCHEME_INDEX,
                            NATURE_INDEX,
                            OBJECTIVE_INDEX,
                            RCN_INDEX
                    ).contains(-1)
            ) {
                System.out.println("Header not found");
                fail();
            }

            // save in batches of 1000
            List<Project> projects = new ArrayList<>();
            for (Row row : sheet) {
                System.out.println("Row: " + row.getRowNum());
                Project project = getProject(row);
                if (project != null) {
                    projects.add(project);
                    if (projects.size() == BATCH_SIZE) {
                        System.out.println("Saving batch of " + projects.size() + " projects");
                        save(projects);
                        projects.clear();
                    }
                } else if (!projects.isEmpty()) {
                    System.out.println("Saving batch of " + projects.size() + " projects");
                    save(projects);
                    projects.clear();
                }
            }
        } catch (IOException | DataIntegrityViolationException e) {
            e.printStackTrace();
            fail();
        }
    }

    private Project getProject(Row row) {
        if (row.getRowNum() == 0) {
            return null; // skip headers
        } else if (row.getCell(ID_INDEX) == null) {
            return null; // skip empty rows
        }

        Project project = Project.builder()
                .referenceId(Long.parseLong(row.getCell(ID_INDEX).getStringCellValue()))
                .rcn(row.getCell(RCN_INDEX).getStringCellValue())
                .acronym(row.getCell(ACRONYM_INDEX).getStringCellValue())
                .title(row.getCell(TITLE_INDEX).getStringCellValue())
                .fundingOrganisation(getFundingOrganisation(TOTAL_COST_INDEX, EC_MAX_CONTRIBUTION_INDEX, row))
                .fundingEU(getFundingEU(row, EC_MAX_CONTRIBUTION_INDEX))
                .status(ProjectStatusEnum.valueOf(row.getCell(STATUS_INDEX).getStringCellValue()))
                .signDate(getDate(EC_SIGNATURE_DATE_INDEX, row))
                .startDate(getDate(START_DATE_INDEX, row))
                .endDate(getDate(END_DATE_INDEX, row))
                .masterCallIdentifier(row.getCell(MASTER_CALL_INDEX).getStringCellValue())
                .legalBasis(row.getCell(LEGAL_BASIS_INDEX).getStringCellValue())
                .fundingScheme(FundingSchemeEnum.valueOfName(row.getCell(FUNDING_SCHEME_INDEX).getStringCellValue()))
                .build();

        // set call
        String callIdentifier = row.getCell(CALL_IDENTIFIER_INDEX).getStringCellValue();
        setMainCall(project, callIdentifier);

        // set long text
        project.setLongTexts(new ArrayList<>());
        addDescriptionIfPresent(row, OBJECTIVE_INDEX, project, LongTextTypeEnum.PROJECT_OBJECTIVE);

        Optional<Project> existingProjectOptional = projectRepository.findByReferenceId(project.getReferenceId());
        if (existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();

            for (LongText longText : project.getLongTexts()) {
                if (existingProject.getLongTexts().stream().noneMatch(lt -> lt.getType().equals(longText.getType()))) {
                    longText.setProject(existingProject);
                    existingProject.getLongTexts().add(longText);
                } else {
                    LongText longTextToSave = existingProject.getLongTexts().stream()
                            .filter(lt -> lt.getType().equals(longText.getType()))
                            .findFirst()
                            .orElseThrow();
                    if (
                            isNotEmpty(longTextToSave.getText())) {
                        longTextToSave.setText(longText.getText());
                    }
                }
            }
            if (isNotEmpty(existingProject.getReferenceId())) {
                existingProject.setReferenceId(project.getReferenceId());
            }
            if (isNotEmpty(existingProject.getRcn())) {
                existingProject.setRcn(project.getRcn());
            }
            if (isNotEmpty(existingProject.getAcronym())) {
                existingProject.setAcronym(project.getAcronym());
            }
            if (isNotEmpty(existingProject.getTitle())) {
                existingProject.setTitle(existingProject.getTitle());
            }
            if (isNotEmpty(existingProject.getFundingOrganisation())) {
                existingProject.setFundingOrganisation(project.getFundingOrganisation());
            }
            if (isNotEmpty(existingProject.getFundingEU())) {
                existingProject.setFundingEU(project.getFundingEU());
            }
            if (isNotEmpty(existingProject.getStatus())) {
                existingProject.setStatus(project.getStatus());
            }
            if (isNotEmpty(existingProject.getSignDate())) {
                existingProject.setSignDate(project.getSignDate());
            }
            if (isNotEmpty(existingProject.getStartDate())) {
                existingProject.setStartDate(project.getStartDate());
            }
            if (isNotEmpty(existingProject.getEndDate())) {
                existingProject.setEndDate(project.getEndDate());
            }
            if (isNotEmpty(existingProject.getCall())) {
                existingProject.setCall(project.getCall());
            }
            if (isNotEmpty(existingProject.getMasterCallIdentifier())) {
                existingProject.setMasterCallIdentifier(project.getMasterCallIdentifier());
            }
            if (isNotEmpty(existingProject.getLegalBasis())) {
                existingProject.setLegalBasis(project.getLegalBasis());
            }
            if (isNotEmpty(existingProject.getFundingScheme())) {
                existingProject.setFundingScheme(project.getFundingScheme());
            }
            return existingProject;
        } else {
            return project;
        }
    }

    public static BigDecimal getFundingEU(Row row, int index) {
        return new BigDecimal(getBudget(index, row)).stripTrailingZeros();
    }

    private void save(List<Project> projects) {
        List<Project> savedProject = projectRepository.saveAll(projects);
        projectSolrClientService.add(SolrMapper.mapToSolrDocument(savedProject), 100_000);
    }

    private void setMainCall(Project project, String callIdentifier) {
        Call existingCall = getMainCall(callIdentifier);
        if (existingCall != null) {
            project.setCall(existingCall);
            existingCall.getProjects().add(project);
        }
    }

    @Nullable
    private Call getMainCall(String callIdentifier) {
        List<Call> existingCalls = callRepository.findyIdentifier(callIdentifier);
        Call existingCall;
        if (existingCalls.size() > 1) {
            existingCall = existingCalls.stream().filter(call -> call.getUrlType().equals(UrlTypeEnum.TOPIC_DETAILS)).findFirst().orElse(existingCalls.get(0));
        } else if (existingCalls.size() == 1) {
            existingCall = existingCalls.get(0);
        } else {
            existingCall = null;
        }
        return existingCall;
    }

    public static BigDecimal getFundingOrganisation(int TOTAL_COST_INDEX, int EC_MAX_CONTRIBUTION_INDEX, Row row) {
        BigDecimal totalCost = new BigDecimal(getBudget(TOTAL_COST_INDEX, row));
        BigDecimal ecMaxContribution = new BigDecimal(getBudget(EC_MAX_CONTRIBUTION_INDEX, row));
        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.stripTrailingZeros();
        } else if (ecMaxContribution.compareTo(totalCost) == 0) {
            return BigDecimal.ZERO.stripTrailingZeros();
        } else {
            return totalCost.subtract(ecMaxContribution).stripTrailingZeros();
        }
    }

    public static String getBudget(int budgetIndex, Row row) {
        String budget;
        Cell budgetCell = row.getCell(budgetIndex);
        if (budgetCell == null) {
            return "0";
        }
        if (budgetCell.getCellType() == CellType.NUMERIC) {
            budget = String.valueOf(budgetCell.getNumericCellValue());
        } else {
            budget = budgetCell.getStringCellValue().replace(",", ".");
        }
        return budget;
    }

    private static LocalDate getDate(int submissionDLIndex, Row row) {
        if (row.getCell(submissionDLIndex) == null || row.getCell(submissionDLIndex).getStringCellValue() == null) {
            return null;
        }
        return DateMapper.mapToDate(row.getCell(submissionDLIndex).getStringCellValue());
    }

    private static void addDescriptionIfPresent(Row row, int index, Project project, LongTextTypeEnum description) {
        if (isNotEmpty(row.getCell(index).getStringCellValue())) {
            LongText longText = LongText.builder().type(description).text(row.getCell(index).getStringCellValue()).build();
            project.getLongTexts().add(longText);
            longText.setProject(project);
        }
    }

}
