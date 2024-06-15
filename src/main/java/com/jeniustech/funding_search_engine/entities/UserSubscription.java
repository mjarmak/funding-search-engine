package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.PaymentStatusEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionJoinType;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
@Table(name = "subscription")
@Builder
@Entity
@RequiredArgsConstructor
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

    @OneToMany(mappedBy = "subscription")
    private List<Payment> payments;

    @OneToMany(mappedBy = "subscription")
    private List<UserSubscriptionJoin> userSubscriptionJoins;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public boolean isPaid() {
        if (payments == null || payments.isEmpty()) {
            return false;
        }
        return payments.stream().anyMatch(payment ->
                payment.getStatus().equals(PaymentStatusEnum.PAID) &&
                        payment.getEndDate().after(new Timestamp(System.currentTimeMillis()))
        );
    }

    public boolean isTrial() {
        return type.equals(SubscriptionTypeEnum.TRIAL);
    }

    public boolean isExpired() {
        if (payments == null || payments.isEmpty()) {
            return true;
        }
        return payments.stream().noneMatch(payment ->
                payment.getStatus().equals(PaymentStatusEnum.PAID) &&
                        payment.getEndDate().after(new Timestamp(System.currentTimeMillis()))
        );
    }

    public boolean isActive() {
        return isPaid() || isTrial();
    }

    public Payment getLatestPayment() {
        if (payments == null || payments.isEmpty()) {
            return null;
        }
        return payments.stream().max(Comparator.comparing(Payment::getStartDate)).get();
    }

    public LocalDateTime getStartDate() {
        Payment latestPayment = getLatestPayment();
        if (latestPayment == null) {
            return null;
        }
        return latestPayment.getStartDate().toLocalDateTime();
    }

    public LocalDateTime getEndDate() {
        Payment latestPayment = getLatestPayment();
        if (latestPayment == null) {
            return null;
        }
        return latestPayment.getEndDate().toLocalDateTime();
    }

    public boolean isAdmin(UserData userData) {
        return userSubscriptionJoins.stream().anyMatch(
                userSubscriptionJoin -> userSubscriptionJoin.getUserData().equals(userData) &&
                        userSubscriptionJoin.getType().equals(SubscriptionJoinType.ADMIN)
        );
    }
}
