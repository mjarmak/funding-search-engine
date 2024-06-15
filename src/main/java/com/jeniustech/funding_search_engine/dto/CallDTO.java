package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Value
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class CallDTO {
    Long id;
    String identifier;
    String title;

    Map<LongTextTypeEnum, String> longTexts;

    String displayDescription;

    String actionType;
    LocalDateTime endDate;
    LocalDateTime endDate2;
    LocalDateTime startDate;
    String budgetMin;
    String budgetMax;
    Short projectNumber;
    String urlId;
    UrlTypeEnum urlType;

    String typeOfMGADescription;
}
