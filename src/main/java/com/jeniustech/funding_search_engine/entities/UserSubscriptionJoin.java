package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.SubscriptionJoinType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_subscription_join")
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

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public boolean isAdmin() {
        return getType().equals(SubscriptionJoinType.ADMIN);
    }

}
