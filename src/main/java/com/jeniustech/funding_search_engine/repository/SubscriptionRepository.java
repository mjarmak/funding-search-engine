package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByCheckoutSessionId(String id);
    Optional<UserSubscription> findByStripeId(String id);
}
