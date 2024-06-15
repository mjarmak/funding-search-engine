package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.SubscriptionDTO;
import com.jeniustech.funding_search_engine.entities.UserSubscription;

public interface SubscriptionMapper {
    static SubscriptionDTO map(UserSubscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .type(subscription.getType())
                .isPaid(subscription.isPaid())
                .isActive(subscription.isActive())
                .build();
    }
}
