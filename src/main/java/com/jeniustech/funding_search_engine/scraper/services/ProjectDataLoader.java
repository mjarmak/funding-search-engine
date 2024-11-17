package com.jeniustech.funding_search_engine.scraper.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.scraper.constants.excel.ProjectCSVColumns;
import com.jeniustech.funding_search_engine.scraper.util.CSVSplitter;
import com.jeniustech.funding_search_engine.services.CSVService;
import com.jeniustech.funding_search_engine.services.solr.ProjectSolrClientService;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.scraper.services.OrganisationDataLoader.getBudgetString;
import static com.jeniustech.funding_search_engine.util.StringUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectDataLoader {

    private final CallRepository callRepository;
    private final ProjectRepository projectRepository;
    private final ProjectSolrClientService projectSolrClientService;
    private final CSVService csvService;

    public static final int BATCH_SIZE = 1000;
    private int total = 0;
    private int problems = 0;
    private String[] headers;
    private final List<String[]> problemRows = new ArrayList<>();

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
    int FRAMEWORK_PROGRAM_INDEX = -1;
    int SUB_CALL_INDEX = -1;
    int FUNDING_SCHEME_INDEX = -1;
    int OBJECTIVE_INDEX = -1;
    int RCN_INDEX = -1;

    public void splitFileAndLoadData(String fileName, boolean oldFormat, boolean skipUpdate, int rowsPerFile, boolean onlyValidate, boolean forceSaveProblems) {
        total = 0;
        problems = 0;
        problemRows.clear();
        resetIndexes();

        fileName = csvService.preprocessCSV(fileName, oldFormat);

        List<String> splitFileNames = CSVSplitter.splitCSVFile(fileName, rowsPerFile);

        for (String splitFileName : splitFileNames) {
            log.info("Loading data from " + splitFileName);
            loadData(splitFileName, oldFormat, skipUpdate, onlyValidate, forceSaveProblems);
        }
        if ((forceSaveProblems || onlyValidate) && problems > 0) {
            log.error("Writing problems to file");
            csvService.writeCSV(headers, problemRows, fileName.replace(".csv", "_invalid_fields.csv"));
        }
    }

    public void loadSolrData(List<FrameworkProgramEnum> frameworkPrograms) {
        log.info("Loading projects to solr");
        // do in batch of 1000
        int pageNumber = 0;
        int pageSize = BATCH_SIZE;
        Sort sort = Sort.sort(Project.class).by(Project::getId).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Project> projects = projectRepository.findAllByFrameworkProgram(
                frameworkPrograms,
                pageable);
        while (!projects.isEmpty()) {
            log.info("Saving batch of " + projects.size() + " items");
            projectSolrClientService.add(SolrMapper.mapProjectsToSolrDocument(projects), 100_000);
            pageNumber++;
            projects = projectRepository.findAll(PageRequest.of(pageNumber, pageSize, sort)).getContent();
        }
    }

    private void loadData(String fileName, boolean oldFormat, boolean skipUpdate, boolean onlyValidate, boolean forceSaveProblems) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(CSVService.DELIMITER_DEFAULT)
                        .withQuoteChar(CSVService.QUOTE)
                        .withEscapeChar('\\')
                        .build()
                ).build()
        ) {
            headers = reader.readNext();
            int index = 0;
            for (String cell : headers) {
                switch (cell) {
                    case ProjectCSVColumns.ID -> ID_INDEX = index;
                    case ProjectCSVColumns.ACRONYM -> ACRONYM_INDEX = index;
                    case ProjectCSVColumns.STATUS -> STATUS_INDEX = index;
                    case ProjectCSVColumns.TITLE -> TITLE_INDEX = index;
                    case ProjectCSVColumns.START_DATE -> START_DATE_INDEX = index;
                    case ProjectCSVColumns.END_DATE -> END_DATE_INDEX = index;
                    case ProjectCSVColumns.TOTAL_COST -> TOTAL_COST_INDEX = index;
                    case ProjectCSVColumns.EC_MAX_CONTRIBUTION -> EC_MAX_CONTRIBUTION_INDEX = index;
                    case ProjectCSVColumns.LEGAL_BASIS -> LEGAL_BASIS_INDEX = index;
                    case ProjectCSVColumns.CALL_IDENTIFIER -> CALL_IDENTIFIER_INDEX = index;
                    case ProjectCSVColumns.EC_SIGNATURE_DATE -> EC_SIGNATURE_DATE_INDEX = index;

                    case ProjectCSVColumns.MASTER_CALL, ProjectCSVColumns.CALL -> MASTER_CALL_INDEX = index;
                    case ProjectCSVColumns.FRAMEWORK_PROGRAM -> FRAMEWORK_PROGRAM_INDEX = index;

                    case ProjectCSVColumns.SUB_CALL -> SUB_CALL_INDEX = index;
                    case ProjectCSVColumns.FUNDING_SCHEME -> FUNDING_SCHEME_INDEX = index;
                    case ProjectCSVColumns.OBJECTIVE -> OBJECTIVE_INDEX = index;
                    case ProjectCSVColumns.RCN -> RCN_INDEX = index;
                }
                index++;
            }

            final List<Integer> headerIndexes = new ArrayList<>(List.of(
                    ID_INDEX,
                    ACRONYM_INDEX,
                    STATUS_INDEX,
                    TITLE_INDEX,
                    START_DATE_INDEX,
                    END_DATE_INDEX,
                    TOTAL_COST_INDEX,
                    EC_MAX_CONTRIBUTION_INDEX,
                    CALL_IDENTIFIER_INDEX,
                    FUNDING_SCHEME_INDEX,
                    FRAMEWORK_PROGRAM_INDEX,
                    OBJECTIVE_INDEX,
                    RCN_INDEX
            ));
            if (!oldFormat) {
                headerIndexes.add(LEGAL_BASIS_INDEX);
                headerIndexes.add(EC_SIGNATURE_DATE_INDEX);
            }
            if (
                    headerIndexes.contains(-1) ||
                            (MASTER_CALL_INDEX == -1 && SUB_CALL_INDEX == -1)
            ) {
                log.error("Header not found, " + headerIndexes.stream().filter(i -> i == -1).map(String::valueOf).count() + " missing");
                throw new ScraperException("Header not found");
            }

            // save in batches of 1000
            List<Project> projects = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                Project project = getProject(row, skipUpdate, onlyValidate);
                if (project == null) {
                    continue;
                }
                total++;
                if (!onlyValidate) {
                    if (!project.isFieldsValid()) {
                        problems++;
                        log.error("Invalid fields for project: " + project.getReferenceId() + " " + project.getTitle());
                        if (forceSaveProblems) {
                            problemRows.add(row);
                        }
                    } else if (!(skipUpdate && project.getId() != null)) {
                        processSave(projects, project, fileName);
                    }
                } else {
                    validateFields(project, row);
                    if (total % 1000 == 0) {
                        log.info("Validated " + total + " items");
                    }
                }
            }
            if (!onlyValidate) {
                log.info("Saving last batch of " + projects.size() + " items");
                save(projects, fileName);
            }
            log.info("Total: " + total + ", problems: " + problems);
        } catch (IOException | DataIntegrityViolationException | CsvValidationException e) {
            log.error(e.getMessage());
            throw new ScraperException(e.getMessage());
        }
    }

    private void validateFields(Project project, String[] row) {
        if (project == null) {
            return;
        }
        if (!project.isFieldsValid()) {
            problems++;
            log.error("Invalid fields for project: " + project.getReferenceId() + " " + project.getTitle());
            problemRows.add(row);
        }
    }

    private void resetIndexes() {
        ID_INDEX = -1;
        ACRONYM_INDEX = -1;
        STATUS_INDEX = -1;
        TITLE_INDEX = -1;
        START_DATE_INDEX = -1;
        END_DATE_INDEX = -1;
        TOTAL_COST_INDEX = -1;
        EC_MAX_CONTRIBUTION_INDEX = -1;
        LEGAL_BASIS_INDEX = -1;
        CALL_IDENTIFIER_INDEX = -1;
        EC_SIGNATURE_DATE_INDEX = -1;
        MASTER_CALL_INDEX = -1;
        FRAMEWORK_PROGRAM_INDEX = -1;
        SUB_CALL_INDEX = -1;
        FUNDING_SCHEME_INDEX = -1;
        OBJECTIVE_INDEX = -1;
        RCN_INDEX = -1;
    }


    private void processSave(List<Project> items, Project item, String fileName) {
        if (item != null) {
            items.add(item);
            if (items.size() == BATCH_SIZE) {
                log.info("Saving batch of " + items.size() + " items, total: " + total);
                save(items, fileName);
                items.clear();
            }
        } else if (!items.isEmpty()) {
            log.info("Saving batch of " + items.size() + " items, total: " + total);
            save(items, fileName);
            items.clear();
        }
    }

    private Project getProject(String[] row, boolean skipUpdate, boolean onlyValidate) {
        if (row[ID_INDEX] == null) {
            return null; // skip empty rows
        }
        String callIdentifier = row[CALL_IDENTIFIER_INDEX];

        Project project = Project.builder()
                .referenceId(getReferenceId(row))
                .rcn(row[RCN_INDEX])
                .acronym(row[ACRONYM_INDEX])
                .title(row[TITLE_INDEX])
                .fundingOrganisation(getFundingOrganisation(TOTAL_COST_INDEX, EC_MAX_CONTRIBUTION_INDEX, row))
                .fundingEU(getBudget(row, EC_MAX_CONTRIBUTION_INDEX))
                .frameworkProgram(FrameworkProgramEnum.valueFrom(row[FRAMEWORK_PROGRAM_INDEX]))
                .status(ProjectStatusEnum.valueFrom(row[STATUS_INDEX]))
                .signDate(EC_SIGNATURE_DATE_INDEX == -1 ? null : getDate(EC_SIGNATURE_DATE_INDEX, row))
                .startDate(getDate(START_DATE_INDEX, row))
                .endDate(getDate(END_DATE_INDEX, row))
                .callIdentifier(valueOrDefault(callIdentifier, null))
                .masterCallIdentifier(valueOrDefault(getMasterCallIdentifier(row), null))
                .legalBasis(LEGAL_BASIS_INDEX == -1 ? null : row[LEGAL_BASIS_INDEX])
                .fundingScheme(FundingSchemeEnum.valueOfName(row[FUNDING_SCHEME_INDEX]))
                .build();

        // set call
        if (!onlyValidate) {
            setMainCall(project, callIdentifier);
        }

        // set long text
        project.setLongTexts(new ArrayList<>());
        addDescriptionIfPresent(row, OBJECTIVE_INDEX, project, LongTextTypeEnum.PROJECT_OBJECTIVE);

        if (onlyValidate) {
            return project;
        }

        Optional<Project> existingProjectOptional = findProject(row);
        if (existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();

            if (skipUpdate) {
                return existingProject;
            }

            // remove because never updated and sometimes invalid
//            for (LongText longText : project.getLongTexts()) {
//                if (existingProject.getLongTexts().stream().noneMatch(lt -> lt.getType().equals(longText.getType()))) {
//                    longText.setProject(existingProject);
//                    existingProject.getLongTexts().add(longText);
//                } else {
//                    LongText longTextToSave = existingProject.getLongTexts().stream()
//                            .filter(lt -> lt.getType().equals(longText.getType()))
//                            .findFirst()
//                            .orElseThrow();
//                    if (
//                            isNotEmpty(longTextToSave.getText())) {
//                        longTextToSave.setText(longText.getText());
//                    }
//                }
//            }
            if (isNotEmpty(project.getReferenceId())) {
                existingProject.setReferenceId(project.getReferenceId());
            }
            // remove because never updated and sometimes invalid
//            if (isNotEmpty(project.getRcn())) {
//                existingProject.setRcn(project.getRcn());
//            }
            if (isNotEmpty(project.getAcronym())) {
                existingProject.setAcronym(project.getAcronym());
            }
            if (isNotEmpty(project.getTitle())) {
                existingProject.setTitle(project.getTitle());
            }
            if (isNotEmpty(project.getFundingOrganisation())) {
                existingProject.setFundingOrganisation(project.getFundingOrganisation());
            }
            if (isNotEmpty(project.getFundingEU())) {
                existingProject.setFundingEU(project.getFundingEU());
            }
            if (isNotEmpty(project.getStatus())) {
                existingProject.setStatus(project.getStatus());
            }
            if (isNotEmpty(project.getSignDate())) {
                existingProject.setSignDate(project.getSignDate());
            }
            if (isNotEmpty(project.getStartDate())) {
                existingProject.setStartDate(project.getStartDate());
            }
            if (isNotEmpty(project.getEndDate())) {
                existingProject.setEndDate(project.getEndDate());
            }
            if (isNotEmpty(project.getCall())) {
                existingProject.setCall(project.getCall());
            }
            if (isNotEmpty(project.getMasterCallIdentifier())) {
                existingProject.setMasterCallIdentifier(project.getMasterCallIdentifier());
            }
            if (isNotEmpty(project.getLegalBasis())) {
                existingProject.setLegalBasis(project.getLegalBasis());
            }
            if (isNotEmpty(project.getFundingScheme())) {
                existingProject.setFundingScheme(project.getFundingScheme());
            }
            if (isNotEmpty(project.getCallIdentifier())) {
                existingProject.setCallIdentifier(project.getCallIdentifier());
            }
            if (project.getFrameworkProgram() != null) {
                existingProject.setFrameworkProgram(project.getFrameworkProgram());
            }
            existingProject.setUpdatedAt(DateMapper.map(LocalDateTime.now()));
            return existingProject;
        } else {
            return project;
        }
    }

    private Optional<Project> findProject(String[] row) {
        String referenceId = row[ID_INDEX];
        Optional<Project> existingProject = projectRepository.findByReferenceId(referenceId);
        if (existingProject.isEmpty()) {
            String rcn = row[RCN_INDEX];
            return projectRepository.findByRcn(rcn);
        }
        return existingProject;
    }
    private String getReferenceId(String[] row) {
        String referenceId = row[ID_INDEX];
        if (isEmpty(referenceId)) {
            throw new ScraperException("Reference ID is empty");
        }
        return referenceId;
    }

    private String getMasterCallIdentifier(String[] row) {
        String masterCallIdentifier;
        if (isEmpty(row[MASTER_CALL_INDEX]) && SUB_CALL_INDEX != -1 && isNotEmpty(row[SUB_CALL_INDEX])) {
            masterCallIdentifier = row[SUB_CALL_INDEX];
        } else {
            masterCallIdentifier = row[MASTER_CALL_INDEX];
        }
        return masterCallIdentifier;
    }

    private void save(List<Project> projects, String fileName) {
        if (projects.isEmpty()) {
            return;
        }
        try {
            projectRepository.saveAll(projects);
        } catch (Exception e) {
            log.error(e.getMessage());
//            e.printStackTrace();
            csvService.writeProjectsCSV(projects, fileName);
            throw new ScraperException(e.getMessage());
        }
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

    public static BigDecimal getFundingOrganisation(int TOTAL_COST_INDEX, int EC_MAX_CONTRIBUTION_INDEX, String[] row) {
        BigDecimal totalCost = new BigDecimal(getBudgetString(TOTAL_COST_INDEX, row));
        BigDecimal ecMaxContribution = new BigDecimal(getBudgetString(EC_MAX_CONTRIBUTION_INDEX, row));
        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else if (ecMaxContribution.compareTo(totalCost) == 0) {
            return null;
        } else {
            return totalCost.subtract(ecMaxContribution).stripTrailingZeros();
        }
    }

    public static BigDecimal getBudget(String[] row, int index) {
        BigDecimal budget = new BigDecimal(getBudgetString(index, row)).stripTrailingZeros();
        if (budget.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return budget;
    }

    private static LocalDate getDate(int submissionDLIndex, String[] row) {
        if (row[submissionDLIndex] == null) {
            return null;
        }
        return DateMapper.mapToDate(row[submissionDLIndex]);
    }

    private static void addDescriptionIfPresent(String[] row, int OBJECTIVE_INDEX, Project project, LongTextTypeEnum description) {
        if (isNotEmpty(row[OBJECTIVE_INDEX])) {
            LongText longText = LongText.builder().type(description).text(row[OBJECTIVE_INDEX]).build();
            project.getLongTexts().add(longText);
            longText.setProject(project);
        }
    }

}
