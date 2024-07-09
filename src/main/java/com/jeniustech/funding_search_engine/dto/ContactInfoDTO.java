package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.ContactInfoTypeEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class ContactInfoDTO {
    private ContactInfoTypeEnum type;
    private String name;
    private String value;
}
