package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.NoSubscriptionException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_data")
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectId;
    private String stripeId;

    private String email;

    private String firstName;

    private String lastName;

    private String userName;

    @Builder.Default
    @OneToMany(mappedBy = "userData", fetch = FetchType.LAZY)
    private List<UserSubscriptionJoin> userSubscriptionJoins = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private BusinessInformation businessInformation;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public String toString() {
        return "UserData(id=" + this.getId() + ", username=" + this.getUserName() + ", email=" + this.getEmail() + ")";
    }

    public List<UserSubscription> getUserSubscriptions() {
        return userSubscriptionJoins.stream().map(UserSubscriptionJoin::getSubscription).toList();
    }

    public boolean hasActiveSubscription() {
        return getUserSubscriptions().stream().anyMatch(UserSubscription::isActive);
    }

    public UserSubscription getMainActiveSubscription() {
        Optional<UserSubscription> nonTrial = getUserSubscriptions().stream().filter(
                        userSubscription -> !userSubscription.getType().equals(SubscriptionTypeEnum.TRIAL) && userSubscription.isActive()
                )
                .findFirst();
        if (nonTrial.isPresent()) {
            return nonTrial.get();
        }
        Optional<UserSubscription> trial = getUserSubscriptions().stream()
                .filter(userSubscription -> userSubscription.getType().equals(SubscriptionTypeEnum.TRIAL) && userSubscription.isActive())
                .findFirst();

        if (trial.isPresent()) {
            return trial.get();
        } else {
            throw new NoSubscriptionException("No active subscription found");
        }
    }

    public UserSubscription getMainSubscription() {
        Optional<UserSubscription> nonTrial = getUserSubscriptions().stream().filter(
                        userSubscription -> !userSubscription.getType().equals(SubscriptionTypeEnum.TRIAL))
                .findFirst();
        if (nonTrial.isPresent()) {
            return nonTrial.get();
        }
        Optional<UserSubscription> trial = getUserSubscriptions().stream()
                .filter(userSubscription -> userSubscription.getType().equals(SubscriptionTypeEnum.TRIAL))
                .findFirst();
        if (trial.isPresent()) {
            return trial.get();
        } else {
            throw new NoSubscriptionException("No subscription found");
        }
    }


    public boolean isAdmin() {
        return getMainSubscription().isAdmin(this);
    }

}
