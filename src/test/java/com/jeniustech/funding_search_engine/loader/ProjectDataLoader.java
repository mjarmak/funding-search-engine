package com.jeniustech.funding_search_engine.loader;

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
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.loader.OrganisationDataLoader.getBudget;
import static com.jeniustech.funding_search_engine.loader.OrganisationDataLoader.getBudgetString;
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

//    @Test
    void loadSolrData() {
        // do in batch of 1000
        int pageNumber = 0;
        int pageSize = 1000;
        Sort sort = Sort.sort(Project.class).by(Project::getId).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Project> projects = projectRepository.findAll(pageable).getContent();
        while (!projects.isEmpty()) {
            projectSolrClientService.add(SolrMapper.mapToSolrDocument(projects), 100_000);
            pageNumber++;
            projects = projectRepository.findAll(PageRequest.of(pageNumber, pageSize, sort)).getContent();
        }
    }

    @Test
    void loadData() {
        String file = "C:\\Projects\\funding-search-engine\\src\\test\\resources\\data\\projects\\project_2021.csv";

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build()
                ).build()
        ) {
            var headers = reader.readNext();
            int index = 0;
            for (String cell : headers) {
                switch (cell) {
                    case ProjectColumns.ID -> ID_INDEX = index;
                    case ProjectColumns.ACRONYM -> ACRONYM_INDEX = index;
                    case ProjectColumns.STATUS -> STATUS_INDEX = index;
                    case ProjectColumns.TITLE -> TITLE_INDEX = index;
                    case ProjectColumns.START_DATE -> START_DATE_INDEX = index;
                    case ProjectColumns.END_DATE -> END_DATE_INDEX = index;
                    case ProjectColumns.TOTAL_COST -> TOTAL_COST_INDEX = index;
                    case ProjectColumns.EC_MAX_CONTRIBUTION -> EC_MAX_CONTRIBUTION_INDEX = index;
                    case ProjectColumns.LEGAL_BASIS -> LEGAL_BASIS_INDEX = index;
                    case ProjectColumns.CALL_IDENTIFIER -> CALL_IDENTIFIER_INDEX = index;
                    case ProjectColumns.EC_SIGNATURE_DATE -> EC_SIGNATURE_DATE_INDEX = index;
                    case ProjectColumns.MASTER_CALL -> MASTER_CALL_INDEX = index;
                    case ProjectColumns.FUNDING_SCHEME -> FUNDING_SCHEME_INDEX = index;
                    case ProjectColumns.NATURE -> NATURE_INDEX = index;
                    case ProjectColumns.OBJECTIVE -> OBJECTIVE_INDEX = index;
                    case ProjectColumns.RCN -> RCN_INDEX = index;
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
            String[] row;
            while ((row = reader.readNext()) != null) {
                Project project = getProject(row);
                processSave(projects, project);
            }
            System.out.println("Saving last batch of " + projects.size() + " items");
            save(projects);
        } catch (IOException | DataIntegrityViolationException | CsvValidationException e) {
            e.printStackTrace();
            fail();
        }
    }

    private void processSave(List<Project> items, Project item) {
        if (item != null) {
            items.add(item);
            if (items.size() == BATCH_SIZE) {
                System.out.println("Saving batch of " + items.size() + " items");
                save(items);
                items.clear();
            }
        } else if (!items.isEmpty()) {
            System.out.println("Saving batch of " + items.size() + " items");
            save(items);
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

    private void save(List<Project> projects) {
        if (projects.isEmpty()) {
            return;
        }
        projectRepository.saveAll(projects);
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
