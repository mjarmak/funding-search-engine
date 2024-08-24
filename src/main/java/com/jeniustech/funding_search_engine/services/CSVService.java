package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.Organisation;
import com.jeniustech.funding_search_engine.entities.Project;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.scraper.models.CallCSVDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jeniustech.funding_search_engine.util.StringUtil.valueOrDefault;

@Service
@Slf4j
public class CSVService {

    public static final char DELIMITER = ';';
    public static final char QUOTE = '\"';

    public String preprocessCSV(String inputFilePath) {
        log.info("Preprocessing CSV file: " + inputFilePath);
        String outputFilePath = inputFilePath.replace(".csv", "_cleaned.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                // Replace triple quotes with single quotes
                line = processCSVLine(line);

                // Write the cleaned line to the output file
                bw.write(line);
                bw.newLine();
            }

            log.info("CSV file has been preprocessed and saved as: " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFilePath;
    }

    String processCSVLine(String line) {
        // Regex to find content within single-quoted strings
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(line);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String quotedContent = matcher.group(1);
            // Escape semicolons within the quoted content
            String escapedContent = quotedContent
                    .replace(";", "\\\\;")
                    .replace("$", "\\$");
            matcher.appendReplacement(sb, "\"" + escapedContent + "\"");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public void writeProjectsCSV(List<Project> projects, String fileName) {
        log.info("Writing failed project rows to csv");
        // write to csv
        String header = "id;acronym;status;title;startDate;endDate;totalCost;ecMaxContribution;legalBasis;topics;ecSignatureDate;masterCall;fundingScheme;objective;rcn";
        StringBuilder csv = new StringBuilder(header);
        for (Project project : projects) {
            String description = project.getLongTexts().stream()
                    .filter(longText -> longText.getType().equals(LongTextTypeEnum.PROJECT_OBJECTIVE))
                    .findFirst()
                    .map(LongText::getText)
                    .orElse("");
            csv.append("\n")
                    .append(QUOTE).append(project.getReferenceId()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(valueOrDefault(project.getAcronym(), "")).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getStatus()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(valueOrDefault(project.getTitle(), "")).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getStartDate() == null ? "" : project.getStartDate()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getEndDate() == null ? "" : project.getEndDate()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getFundingEU().add(project.getFundingOrganisation()).toPlainString()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getFundingEU().toPlainString()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(valueOrDefault(project.getLegalBasis(), "")).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getCall() == null ? "" : project.getCall().getIdentifier()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getSignDate() == null ? "" : project.getSignDate()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(valueOrDefault(project.getMasterCallIdentifier(), "")).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(project.getFundingScheme() == null ? "" : project.getFundingScheme().getName()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(description).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(valueOrDefault(project.getRcn(), "")).append(QUOTE);
        }
        try (FileWriter fileWriter = new FileWriter(fileName.replace(".csv", "_" + System.currentTimeMillis() + "_failed.csv"))) {
            fileWriter.write(csv.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to csv", e);
        }
    }

    public void writePartnersCSV(List<Organisation> organisations, String fileName) {
        log.info("Writing failed partner rows to csv");
        // write to csv
        String header = "organisationID;vatNumber;name;shortName;SME;activityType;street;postCode;city;country;nutsCode;geolocation;organizationURL;contactForm;contentUpdateDate;rcn;order;role;ecContribution;netEcContribution;totalCost;endOfParticipation;active\n";
        StringBuilder csv = new StringBuilder(header);
        for (Organisation organisation : organisations) {
            csv.append("\n")
                    .append(QUOTE).append(organisation.getReferenceId()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(organisation.getVatNumber()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(organisation.getName()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(organisation.getShortName()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(organisation.getSme()).append(QUOTE).append(DELIMITER)
                    .append(QUOTE).append(valueOrDefault(organisation.getRcn(), "")).append(QUOTE);
        }
        try (FileWriter fileWriter = new FileWriter(fileName.replace(".csv", "_" + System.currentTimeMillis() + "_failed.csv"))) {
            fileWriter.write(csv.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to csv", e);
        }
    }

    public void writeCallsCSV(List<Call> calls, String fileName) {
        log.info("Writing failed call rows to csv");
        // write to csv
        String header = String.join(",", CallCSVDetails.getCSVHeaders());
        StringBuilder csv = new StringBuilder(header);
        for (Call call : calls) {
            csv.append("\n")
                    .append(QUOTE).append(call.getIdentifier()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getTitle()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getActionType()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getTypeOfMGADescription()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getSubmissionProcedure()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getStartDate()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getEndDate()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getEndDate2()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getBudgetMin()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getBudgetMax()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getProjectNumber()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getUrlId()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getReference()).append(QUOTE).append(",")
                    .append(QUOTE).append(call.getDescription()).append(QUOTE).append(",")
                    .append(QUOTE).append(valueOrDefault(call.getLongTextByTypeAsString(LongTextTypeEnum.MISSION_DETAILS), "")).append(QUOTE).append(",")
                    .append(QUOTE).append(valueOrDefault(call.getLongTextByTypeAsString(LongTextTypeEnum.DESTINATION_DETAILS), "")).append(QUOTE).append(",")
                    .append(QUOTE).append(valueOrDefault(call.getLongTextByTypeAsString(LongTextTypeEnum.BENEFICIARY_ADMINISTRATION), "")).append(QUOTE).append(",")
                    .append(QUOTE).append(valueOrDefault(call.getLongTextByTypeAsString(LongTextTypeEnum.FURTHER_INFORMATION), "")).append(QUOTE).append(",")
                    .append(QUOTE).append(valueOrDefault(call.getLongTextByTypeAsString(LongTextTypeEnum.DURATION), "")).append(QUOTE);
        }
        try (FileWriter fileWriter = new FileWriter(fileName.replace(".csv", "_" + System.currentTimeMillis() + "_failed.csv"))) {
            fileWriter.write(csv.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to csv", e);
        }
    }
}
