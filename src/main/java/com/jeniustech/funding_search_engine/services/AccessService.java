package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.entities.UserSubscriptionJoin;
import com.jeniustech.funding_search_engine.exceptions.NotFoundItemException;
import com.jeniustech.funding_search_engine.repository.SubscriptionRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.repository.UserSubscriptionJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final UserSubscriptionJoinRepository userSubscriptionJoinRepository;

    public boolean isUserAdmin(Long subscriptionId, Long userId) {
        UserSubscriptionJoin subscription = userSubscriptionJoinRepository.findBySubscriptionIdAndUserDataId(subscriptionId, userId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));
        return subscription.isAdmin();
    }

}
