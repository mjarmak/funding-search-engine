package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.NotAllowedActionException;
import com.jeniustech.funding_search_engine.exceptions.NotFoundItemException;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserDataRepository userDataRepository;

    public void validateSubscription(String userId, SubscriptionTypeEnum type) {
        UserData userData = userDataRepository.findBySubjectId(userId)
                .orElseThrow(() -> new NotFoundItemException("User not found"));
        UserSubscription subscription = userData.getMainSubscription();
        if (subscription.getType().equals(type)) {
            throw new NotAllowedActionException("User already has this subscription");
        } else if (subscription.getType().getLevel() > type.getLevel()) {
            throw new NotAllowedActionException("User can't downgrade subscription");
        }
    }
}
