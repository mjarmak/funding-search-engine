package com.jeniustech.funding_search_engine.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class BusinessInformationDTO {
    String name;
    String phoneNumber;
    String email;
    String vatNumber;
    AddressDTO address;
}
