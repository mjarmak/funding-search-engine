package com.jeniustech.funding_search_engine.dto;

import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class PaymentSessionDTO {
    String url;
    String successUrl;
    String cancelUrl;
    SubscriptionTypeEnum type;
}
