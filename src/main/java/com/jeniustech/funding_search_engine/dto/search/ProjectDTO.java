package com.jeniustech.funding_search_engine.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.dto.LongTextDTO;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;
import com.jeniustech.funding_search_engine.enums.ProjectStatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class ProjectDTO extends SearchItemDTO {
    private Long id;
    private Long callId;
    private CallDTO call;
    private List<PartnerDTO> partners;
    private String acronym;
    private String title;
    private String fundingOrganisation;
    private String fundingEU;
    private String totalFundingOrganisation;
    private String totalFundingEU;

    private ProjectStatusEnum status;
    private LocalDateTime signDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String callIdentifier;
    private String masterCallIdentifier;
    private String legalBasis;
    private String fundingScheme;
    private List<LongTextDTO> longTexts;
    private String url;

    private OrganisationProjectJoinTypeEnum joinType;

    private Boolean favorite;
    private Float score;
}
