package com.jeniustech.funding_search_engine.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CountryDTO {

    int id;
    String code;
    String name;

}
