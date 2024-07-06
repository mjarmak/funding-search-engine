package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.FundingSchemeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class ProjectDTO {
    private Long id;
    private Long callId;
    private String callIdentifier;
    private Long referenceId;

    private String acronym;
    private String title;

    private String fundingOrganisation;
    private String fundingEU;

    private ProjectStatusEnum status;

    private LocalDateTime signDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String masterCallIdentifier;

    private String legalBasis;

    private FundingSchemeEnum fundingScheme;

    private List<LongTextDTO> longTexts;

    private float score;
}
