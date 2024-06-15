package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<UserSubscription, Long> {
}
