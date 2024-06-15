package com.jeniustech.funding_search_engine.constants;

import java.time.format.DateTimeFormatter;

public interface Constants {

    DateTimeFormatter csvFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    int displayDescriptionMaxLength = 255;

    String ID = "id";
    String IDENTIFIER = "identifier";
    String TITLE = "title";
    String DESCRIPTION_DISPLAY = "description_display";
    String LONG_TEXT = "long_text";
    String ACTION_TYPE = "action_type";
    String SUBMISSION_DEADLINE_DATE = "submission_deadline_date";
    String SUBMISSION_DEADLINE_DATE_2 = "submission_deadline_date2";
    String OPEN_DATE = "open_date";
    String BUDGET_MIN = "budget_min";
    String BUDGET_MAX = "budget_max";
    String PROJECT_NUMBER = "project_number";
    String URL_ID = "url_id";
    String URL_TYPE = "url_type";

}
