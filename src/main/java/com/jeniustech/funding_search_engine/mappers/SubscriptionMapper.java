package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.SubscriptionDTO;
import com.jeniustech.funding_search_engine.entities.UserSubscription;

public interface SubscriptionMapper {
    static SubscriptionDTO map(UserSubscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .trialEndDate(DateMapper.map(subscription.getTrialEndDate()))
                .endDate(DateMapper.map(subscription.getEndDate()))
                .type(subscription.getType())
                .active(subscription.isActive())
                .build();
    }
}
