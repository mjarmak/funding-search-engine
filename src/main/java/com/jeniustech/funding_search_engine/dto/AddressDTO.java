package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.CountryEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class AddressDTO {
    private String street;
    private String postCode;
    private String city;
    private CountryEnum country;
    private String countryName;
}
