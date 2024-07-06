package com.jeniustech.funding_search_engine.dto;

import com.jeniustech.funding_search_engine.enums.CountryEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PartnerDTO {
    private Long organisationId;
    private Long projectId;
    private String name;
    private OrganisationTypeEnum type;
    private int projectsMatched;
    private CountryEnum country;
    private float maxScore;
}
