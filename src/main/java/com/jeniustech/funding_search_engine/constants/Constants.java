package com.jeniustech.funding_search_engine.constants;

import java.time.format.DateTimeFormatter;

public interface Constants {

    DateTimeFormatter csvFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    int displayDescriptionMaxLength = 255;

    String ID = "id";
    String IDENTIFIER = "identifier";
    String TITLE = "title";
    String DESCRIPTION_DISPLAY = "description_display";
    String DESCRIPTION = "description";
    String DESTINATION_DETAILS = "destination_details";
    String MISSION_DETAILS = "mission_details";
    String ACTION_TYPE = "action_type";
    String SUBMISSION_DEADLINE_DATE = "submission_deadline_date";
    String OPEN_DATE = "open_date";
    String BUDGET = "budget";
    String PROJECT_NUMBER = "project_number";
    String PATH_ID = "path_id";
    String REFERENCE = "reference";

}
