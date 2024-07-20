package com.jeniustech.funding_search_engine.scraper.models;

import lombok.Builder;
import lombok.Value;

import static com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil.processString;

@Builder
@Value
public class CallCSVDetails {
    String identifier;
    String title;
    String description;
    String destinationDetails;
    String missionDetails;

    String typeOfMGADescription;

    String actionType;

    String submissionProcedure;
    String deadline;
    String deadline2;
    String startDate;
    String budgetMin;
    String budgetMax;
    Integer projectNumber;
    String pathId;
    String reference;

    String beneficiaryAdministration;
    String furtherInformation;
    String duration;
    String conditions;
    String supportInformation;

    public static String getHeaders() {
        return "identifier," +
                "title," +
                "action_type," +
                "type_of_mga_description," +
                "submission_procedure," +
                "start_date," +
                "deadline," +
                "deadline2," +
                "budget_min," +
                "budget_max," +
                "project_number," +
                "path_id," +
                "reference," +
                "description," +
                "mission_details," +
                "destination_details," +
                "beneficiary_administration," +
                "further_information," +
                "duration";

    }

    public String toCSV() {
        return String.join(",",
                processString(identifier, false),
                processString(title, false),
                processString(actionType, false),
                processString(typeOfMGADescription, false),
                processString(submissionProcedure, false),
                processString(startDate, true),
                processString(deadline, true),
                processString(deadline2, true),
                processString(budgetMin, false),
                processString(budgetMax, false),
                processString(projectNumber, false),
                processString(pathId, false),
                processString(reference, false),
                processString(description, false),
                processString(missionDetails, false),
                processString(destinationDetails, false),
                processString(beneficiaryAdministration, false),
                processString(furtherInformation, false),
                processString(duration, false)
        );
    }
}
