package com.jeniustech.funding_search_engine.scraper.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.ProjectRepository;
import com.jeniustech.funding_search_engine.scraper.constants.excel.ProjectCSVColumns;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.scraper.services.OrganisationDataLoader.getBudget;
import static com.jeniustech.funding_search_engine.scraper.services.OrganisationDataLoader.getBudgetString;
import static com.jeniustech.funding_search_engine.util.StringUtil.isNotEmpty;

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
    int OBJECTIVE_INDEX = -1;
    int RCN_INDEX = -1;

    public void loadSolrData() {
        log.info("Loading projects to solr");
        // do in batch of 1000
        int pageNumber = 0;
        int pageSize = BATCH_SIZE;
        Sort sort = Sort.sort(Project.class).by(Project::getId).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Project> projects = projectRepository.findAll(pageable).getContent();
        while (!projects.isEmpty()) {
            log.info("Saving batch of " + projects.size() + " items");
            projectSolrClientService.add(SolrMapper.mapToSolrDocument(projects), 100_000);
            pageNumber++;
            projects = projectRepository.findAll(PageRequest.of(pageNumber, pageSize, sort)).getContent();
        }
    }

    public void loadData(String fileName) {
        total = 0;
        fileName = csvService.preprocessCSV(fileName);

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(fileName))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(CSVService.DELIMITER)
                        .withQuoteChar(CSVService.QUOTE)
                        .withEscapeChar('\\')
                        .build()
                ).build()
        ) {
            var headers = reader.readNext();
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
                    case ProjectCSVColumns.MASTER_CALL -> MASTER_CALL_INDEX = index;
                    case ProjectCSVColumns.FUNDING_SCHEME -> FUNDING_SCHEME_INDEX = index;
                    case ProjectCSVColumns.OBJECTIVE -> OBJECTIVE_INDEX = index;
                    case ProjectCSVColumns.RCN -> RCN_INDEX = index;
                }
                index++;
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
                            OBJECTIVE_INDEX,
                            RCN_INDEX
                    ).contains(-1)
            ) {
                log.error("Header not found");
                throw new ScraperException("Header not found");
            }

            // save in batches of 1000
            List<Project> projects = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                Project project = getProject(row);
                processSave(projects, project, fileName);
            }
            log.info("Saving last batch of " + projects.size() + " items");
            save(projects, fileName);
        } catch (IOException | DataIntegrityViolationException | CsvValidationException e) {
            e.printStackTrace();
            throw new ScraperException(e.getMessage());
        }
    }


    private void processSave(List<Project> items, Project item, String fileName) {
        total++;
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

    private Project getProject(String[] row) {
        if (row[ID_INDEX] == null) {
            return null; // skip empty rows
        }

        Project project = Project.builder()
                .referenceId(Long.parseLong(row[ID_INDEX]))
                .rcn(row[RCN_INDEX])
                .acronym(row[ACRONYM_INDEX])
                .title(row[TITLE_INDEX])
                .fundingOrganisation(getFundingOrganisation(TOTAL_COST_INDEX, EC_MAX_CONTRIBUTION_INDEX, row))
                .fundingEU(getBudget(row, EC_MAX_CONTRIBUTION_INDEX))
                .status(ProjectStatusEnum.valueOf(row[STATUS_INDEX]))
                .signDate(getDate(EC_SIGNATURE_DATE_INDEX, row))
                .startDate(getDate(START_DATE_INDEX, row))
                .endDate(getDate(END_DATE_INDEX, row))
                .masterCallIdentifier(row[MASTER_CALL_INDEX])
                .legalBasis(row[LEGAL_BASIS_INDEX])
                .fundingScheme(FundingSchemeEnum.valueOfName(row[FUNDING_SCHEME_INDEX]))
                .build();

        // set call
        String callIdentifier = row[CALL_IDENTIFIER_INDEX];
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
            if (isNotEmpty(project.getReferenceId())) {
                existingProject.setReferenceId(project.getReferenceId());
            }
            if (isNotEmpty(project.getRcn())) {
                existingProject.setRcn(project.getRcn());
            }
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
            return existingProject;
        } else {
            return project;
        }
    }

    private void save(List<Project> projects, String fileName) {
        if (projects.isEmpty()) {
            return;
        }
        try {
            projectRepository.saveAll(projects);
        } catch (Exception e) {
            e.printStackTrace();
            csvService.writeProjectsCSV(projects, fileName);
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
            return BigDecimal.ZERO.stripTrailingZeros();
        } else if (ecMaxContribution.compareTo(totalCost) == 0) {
            return BigDecimal.ZERO.stripTrailingZeros();
        } else {
            return totalCost.subtract(ecMaxContribution).stripTrailingZeros();
        }
    }

    private static LocalDate getDate(int submissionDLIndex, String[] row) {
        if (row[submissionDLIndex] == null) {
            return null;
        }
        return DateMapper.mapToDate(row[submissionDLIndex]);
    }

    private static void addDescriptionIfPresent(String[] row, int index, Project project, LongTextTypeEnum description) {
        if (isNotEmpty(row[index])) {
            LongText longText = LongText.builder().type(description).text(row[index]).build();
            project.getLongTexts().add(longText);
            longText.setProject(project);
        }
    }

}
