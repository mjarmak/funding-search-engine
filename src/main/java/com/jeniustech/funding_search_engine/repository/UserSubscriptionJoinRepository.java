package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserSubscriptionJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionJoinRepository extends JpaRepository<UserSubscriptionJoin, Long> {
    Optional<UserSubscriptionJoin> findBySubscriptionIdAndUserDataId(Long subscriptionId, Long userId);

}
