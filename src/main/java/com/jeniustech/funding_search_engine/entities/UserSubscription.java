package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "admin_user_id")
    private UserData adminUser;

    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    private SubscriptionTypeEnum type = SubscriptionTypeEnum.TRIAL;

    @Enumerated(EnumType.ORDINAL)
    private SubscriptionTypeEnum nextType;

    private Timestamp trialEndDate;
    private Timestamp endDate;

    @OneToMany(mappedBy = "subscription")
    private List<UserSubscriptionJoin> userSubscriptionJoins;

    @Column(length = 255)
    private String checkoutSessionId;

    @Column(length = 50)
    private String stripeId;

    @Column(nullable = false)
    private SubscriptionStatusEnum status;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public boolean isActive() {
        if (type.equals(SubscriptionTypeEnum.TRIAL) && (trialEndDate == null || trialEndDate.before(new Timestamp(System.currentTimeMillis())))) {
            return false;
        } else if (trialEndDate != null && trialEndDate.after(new Timestamp(System.currentTimeMillis()))) {
            return true;
        } else if (status == null) {
            return false;
        } else {
            return status.equals(SubscriptionStatusEnum.ACTIVE);
        }
    }

    public boolean isTrial() {
        return type.getParent().equals(SubscriptionTypeParentEnum.TRIAL);
    }

    public boolean isIndividual() {
        return type.getParent().equals(SubscriptionTypeParentEnum.INDIVIDUAL);
    }

    public boolean isAdmin(UserData userData) {
        return userSubscriptionJoins.stream().anyMatch(
                userSubscriptionJoin -> userSubscriptionJoin.getUserData().equals(userData) &&
                        userSubscriptionJoin.getType().equals(SubscriptionJoinType.ADMIN)
        );
    }

    public void setEndDateFromNow(SubscriptionPeriodEnum period) {
        if (period == null) {
            endDate = null;
            return;
        }
        switch (period) {
            case MONTHLY -> endDate = DateMapper.map(
                    LocalDateTime.now()
                            .plusMonths(1)
                            .plusDays(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)
            );
            case YEARLY -> endDate = DateMapper.map(
                    LocalDateTime.now()
                            .plusYears(1)
                            .plusDays(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)
            );
        }

    }
}
