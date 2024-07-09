package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.BooleanEnum;
import com.jeniustech.funding_search_engine.enums.OrganisationTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class OrganisationDTO {
    private Long id;
    private Long name;
    private Long shortName;
    private AddressDTO address;
    private LocationCoordinatesDTO locationCoordinates;

    private String vatNumber;
    private String nutsCode;
    private BooleanEnum sme;
    private OrganisationTypeEnum type;
    private List<ContactInfoDTO> contactInfos;
}
