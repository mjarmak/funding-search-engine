package com.jeniustech.funding_search_engine.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.dto.AddressDTO;
import com.jeniustech.funding_search_engine.dto.ContactInfoDTO;
import com.jeniustech.funding_search_engine.dto.LocationCoordinatesDTO;
import com.jeniustech.funding_search_engine.enums.OrganisationProjectJoinTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class PartnerDTO extends SearchItemDTO {
    private Long id;
    private String name;
    private String shortName;
    private AddressDTO address;
    private LocationCoordinatesDTO locationCoordinates;
    private String vatNumber;
    private String nutsCode;
    private Boolean sme;
    private String typeName;
    private List<ContactInfoDTO> contactInfos;

    private Integer projectsMatched;
    private Integer maxScore;

    private String fundingOrganisation;
    private String fundingEU;
    private Short projectNumber;
    private OrganisationProjectJoinTypeEnum joinType;

    private Float score;
    private Boolean favorite;
}
