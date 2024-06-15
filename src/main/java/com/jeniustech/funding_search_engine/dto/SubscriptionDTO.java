package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class SubscriptionDTO {
    Long id;
    SubscriptionTypeEnum type;
    LocalDateTime startDate;
    LocalDateTime endDate;
    boolean isPaid;
    boolean isActive;

}
