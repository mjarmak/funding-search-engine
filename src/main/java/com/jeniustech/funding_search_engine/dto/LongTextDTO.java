package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class LongTextDTO {
    LongTextTypeEnum type;
    String text;
}
