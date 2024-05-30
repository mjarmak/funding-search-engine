package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.ActionTypeEnum;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Value
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class CallDTO {
    Long id;
    String identifier;
    String title;
    String description;
    String displayDescription;
    ActionTypeEnum actionType;
    LocalDate submissionDeadlineDate;
    LocalDate openDate;
    String budget;
    Short projectNumber;
}
