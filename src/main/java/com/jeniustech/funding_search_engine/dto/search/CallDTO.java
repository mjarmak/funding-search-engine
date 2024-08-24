package com.jeniustech.funding_search_engine.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.dto.LongTextDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class CallDTO extends SearchItemDTO {
    private Long id;
    private String identifier;
    private String title;

    private List<LongTextDTO> longTexts;
    private List<ProjectDTO> projects;

    private String displayDescription;

    private String actionType;
    private LocalDateTime endDate;
    private LocalDateTime endDate2;
    private LocalDateTime startDate;
    private String budgetMin;
    private String budgetMax;
    private Short projectNumber;
    private String url;

    private String typeOfMGADescription;

    private boolean favorite;
    private float score;
}
