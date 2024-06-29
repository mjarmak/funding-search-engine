package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class CallDTO {
    private Long id;
    private String identifier;
    private String title;

    private List<LongTextDTO> longTexts;

    private String displayDescription;

    private String actionType;
    private LocalDateTime endDate;
    private LocalDateTime endDate2;
    private LocalDateTime startDate;
    private String budgetMin;
    private String budgetMax;
    private Short projectNumber;
    private float score;
    private String url;

    private String typeOfMGADescription;

    private boolean favorite;
}
