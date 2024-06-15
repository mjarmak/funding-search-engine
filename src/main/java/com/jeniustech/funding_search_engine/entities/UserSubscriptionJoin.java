package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.SubscriptionJoinType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Table(name = "user_subscription_join")
@Builder
@Entity
@RequiredArgsConstructor
public class UserSubscriptionJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private UserSubscription subscription;

    @Enumerated(EnumType.ORDINAL)
    private SubscriptionJoinType type;

    public boolean isAdmin() {
        return getType().equals(SubscriptionJoinType.ADMIN);
    }

}
