package com.jeniustech.funding_search_engine.scraper.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.SolrMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.scraper.constants.excel.CallCSVColumns;
import com.jeniustech.funding_search_engine.services.CSVService;
import com.jeniustech.funding_search_engine.services.NLPService;
import com.jeniustech.funding_search_engine.services.solr.CallSolrClientService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jeniustech.funding_search_engine.util.StringUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallDataLoader {

    private final CallRepository callRepository;
    private final CallSolrClientService callSolrClientService;
    private final NLPService nlpService;
    private final CSVService csvService;

    public static final int BATCH_SIZE = 1000;
    private int total = 0;

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

    public void loadSolrData() {
        loadSolrData(null);
    }
    public void loadSolrData(LocalDateTime after) {
        log.info("Loading calls to solr");
        // do in batch of 1000
        int pageNumber = 0;
        int pageSize = 1000;
        Sort sort = Sort.sort(Project.class).by(Project::getId).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        List<Call> calls;
        if (after != null) {
            calls = callRepository.findAllAfter(pageable, DateMapper.map(after)).getContent();
        } else {
            calls = callRepository.findAll(pageable).getContent();
        }
        while (!calls.isEmpty()) {
            log.info("Saving batch of " + calls.size() + " items");
            callSolrClientService.add(SolrMapper.mapCallsToSolrDocument(calls), 100_000);
            pageNumber++;
            if (after != null) {
                calls = callRepository.findAllAfter(PageRequest.of(pageNumber, pageSize, sort), DateMapper.map(after)).getContent();
            } else {
                calls = callRepository.findAll(PageRequest.of(pageNumber, pageSize, sort)).getContent();
            }
        }
    }

    public void updateProjectNumbers() {
        log.info("Updating project numbers");
        int pageNumber = 0;
        int pageSize = 1000;
        Sort sort = Sort.sort(Project.class).by(Project::getId).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Call> calls = callRepository.findAll(pageable).getContent();
        while (!calls.isEmpty()) {
            log.info("Updating project numbers for batch of " + calls.size() + " items");
            for (Call call : calls) {
                if (call.getProjectNumber() == null || call.getProjectNumber() == 0) {
                    call.setProjectNumber((short) call.getProjects().size());
                }
            }
            callRepository.saveAll(calls);
            pageNumber++;
            calls = callRepository.findAll(PageRequest.of(pageNumber, pageSize, sort)).getContent();
        }
    }

    public void loadData(String fileName, boolean skipUpdate, boolean secret) {
        total = 0;

        resetIndexes();

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            // get headers
            var headers = reader.readNext();
            int index = 0;
            for (String cell : headers) {
                switch (cell) {
                    case CallCSVColumns.IDENTIFIER -> identifierIndex = index;
                    case CallCSVColumns.TITLE -> titleIndex = index;
                    case CallCSVColumns.SUBMISSION_DL -> submissionDLIndex = index;
                    case CallCSVColumns.SUBMISSION_DL2 -> submissionDL2Index = index;
                    case CallCSVColumns.ACTION_TYPE -> actionTypeIndex = index;
                    case CallCSVColumns.DATE_OPEN -> openDateIndex = index;
                    case CallCSVColumns.BUDGET_MIN -> budgetMinIndex = index;
                    case CallCSVColumns.BUDGET_MAX -> budgetMaxIndex = index;
                    case CallCSVColumns.DESCRIPTION -> descriptionIndex = index;
                    case CallCSVColumns.NUMBER_OF_PROJECTS -> numberOfProjectsIndex = index;
                    case CallCSVColumns.PATH_ID -> pathIdIndex = index;
                    case CallCSVColumns.REFERENCE -> referenceIndex = index;
                    case CallCSVColumns.DESTINATION_DETAILS -> destinationDetailsIndex = index;
                    case CallCSVColumns.MISSION_DETAILS -> missionDetailsIndex = index;
                    case CallCSVColumns.TYPE_OF_MGA_DESCRIPTION -> typeOfMGADescriptionIndex = index;
                    case CallCSVColumns.SUBMISSION_PROCEDURE -> submissionProcedureIndex = index;
                    case CallCSVColumns.BENEFICIARY_ADMINISTRATION -> beneficiaryAdministrationIndex = index;
                    case CallCSVColumns.DURATION -> durationIndex = index;
                    case CallCSVColumns.FURTHER_INFORMATION -> furtherInformationIndex = index;
                }
                index++;
            }

            final List<Integer> headerIndexes = List.of(
                    titleIndex,
                    submissionDLIndex,
                    submissionDL2Index,
                    actionTypeIndex,
                    openDateIndex,
                    budgetMinIndex,
                    budgetMaxIndex,
                    descriptionIndex,
                    destinationDetailsIndex,
                    missionDetailsIndex,
                    numberOfProjectsIndex,
                    pathIdIndex,
                    referenceIndex,
                    typeOfMGADescriptionIndex,
                    submissionProcedureIndex,
                    beneficiaryAdministrationIndex,
                    durationIndex,
                    furtherInformationIndex
            );
            if (
                    headerIndexes.contains(-1)
            ) {
                log.error("Header not found, " + headerIndexes.stream().filter(i -> i == -1).map(String::valueOf).count() + " missing");
                throw new ScraperException("Header not found");
            }

            // save in batches of 1000
            List<Call> calls = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                Call call = getCall(row, skipUpdate, secret);
                processSave(calls, call, fileName);
            }
            log.info("Saving last batch of " + calls.size() + " items");
            save(calls, fileName);
        } catch (IOException | ArrayIndexOutOfBoundsException | CsvValidationException e) {
            e.printStackTrace();
            throw new ScraperException(e.getMessage());
        }
    }

    private void resetIndexes() {
        identifierIndex = 0;
        titleIndex = 0;
        submissionDLIndex = 0;
        submissionDL2Index = 0;
        actionTypeIndex = 0;
        openDateIndex = 0;
        budgetMinIndex = 0;
        budgetMaxIndex = 0;
        descriptionIndex = 0;
        destinationDetailsIndex = 0;
        missionDetailsIndex = 0;
        numberOfProjectsIndex = 0;
        pathIdIndex = 0;
        referenceIndex = 0;
        typeOfMGADescriptionIndex = 0;
        submissionProcedureIndex = 0;
        beneficiaryAdministrationIndex = 0;
        durationIndex = 0;
        furtherInformationIndex = 0;
    }

    private void processSave(List<Call> items, Call item, String fileName) {
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
    private void save(List<Call> calls, String fileName) {
        if (calls.isEmpty()) {
            return;
        }
        try {
            callRepository.saveAll(calls);
        } catch (Exception e) {
            e.printStackTrace();
            csvService.writeCallsCSV(calls, fileName);
        }
    }

    private Call getCall(String[] row, boolean skipUpdate, boolean secret) throws IOException {
        if (row[identifierIndex] == null || row[identifierIndex].isEmpty()) {
            return null;
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
                .budgetMin(getBudgetFromString(row, budgetMinIndex))
                .budgetMax(getBudgetFromString(row, budgetMaxIndex))
                .projectNumber(getProjectNumber(row[numberOfProjectsIndex]))
                .typeOfMGADescription(valueOrDefault(row[typeOfMGADescriptionIndex], null))
                .build();

        call.setSecret(secret);

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

                Call existingCall = existingCallOptional.get();

                if (skipUpdate) {
                    return existingCall;
                } else {
                    existingCall.setUpdatedAt(DateMapper.map(LocalDateTime.now()));
                }

                log.debug("Updating call: " + call.getIdentifier());
                for (LongText longText : call.getLongTexts()) {
                    Optional<LongText> existingLongText = existingCall.getLongTexts().stream()
                            .filter(lt -> lt.getType().equals(longText.getType()))
                            .findFirst();
                    if (existingLongText.isEmpty()) {
                        longText.setCall(existingCall);
                        existingCall.getLongTexts().add(longText);
                    } else {
                        if (
                                !existingLongText.get().getText().equals(longText.getText())
                        ) {
                            existingLongText.get().setText(longText.getText());
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
                if (isEmpty(existingCall.getKeywords())) {
                    setKeywords(existingCall);
                }
                existingCall.setSecret(secret);
                existingCall.setUpdatedAt(DateMapper.map(LocalDateTime.now()));
                return existingCall;
            } else {
                log.debug("Adding call: " + call.getIdentifier());
                setKeywords(call);
                return call;
            }
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new ScraperException("Data integrity violation");
        }
    }

    private BigDecimal getBudgetFromString(String[] row, int index) {
        if (index == -1 || isEmpty(row[index])) {
            return null;
        }
        return new BigDecimal(row[index]).stripTrailingZeros();
    }

    private void setKeywords(Call call) throws IOException {
        String text = call.getAllText();
        List<String> keywords = nlpService.getKeywords(text);
        call.setKeywords(String.join(" ", keywords));
    }

    private static void addDescriptionIfPresent(String[] row, int descriptionIndex, Call call, LongTextTypeEnum description) {
        if (isNotEmpty(row[descriptionIndex])) {
            call.getLongTexts().add(LongText.builder().type(description).text(row[descriptionIndex]).build());
        }
    }

    private static Short getProjectNumber(String row) {
        if (isEmpty(row)) {
            return null;
        }
        return Short.parseShort(row);
    }

}
