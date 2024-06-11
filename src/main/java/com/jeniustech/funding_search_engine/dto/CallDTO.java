package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class CallDTO {
    Long id;
    String identifier;
    String title;
    String description;
    String displayDescription;
    String actionType;
    LocalDateTime submissionDeadlineDate;
    LocalDateTime submissionDeadlineDate2;
    LocalDateTime openDate;
    String budget;
    Short projectNumber;
    String pathId;
    String reference;

    SubmissionProcedureEnum submissionProcedure;
    String destinationDetails;
    String missionDetails;
    String typeOfMGA;
    String typeOfMGADescription;
}
