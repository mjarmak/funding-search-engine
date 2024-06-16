package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.NoSubscriptionException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

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

    private String email;

    private String firstName;

    private String lastName;

    private String userName;

    @OneToMany(mappedBy = "userData")
    private List<UserSubscriptionJoin> userSubscriptionJoins;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public List<UserSubscription> getUserSubscriptions() {
        return userSubscriptionJoins.stream().map(UserSubscriptionJoin::getSubscription).toList();
    }

    public UserSubscription getMainActiveSubscription() {
        return getUserSubscriptions().stream().filter(
            userSubscription -> !userSubscription.getType().equals(SubscriptionTypeEnum.TRIAL) && userSubscription.isPaid()
        )
                .findFirst().orElse(
            getUserSubscriptions().stream()
                .filter(userSubscription -> userSubscription.getType().equals(SubscriptionTypeEnum.TRIAL) && userSubscription.isPaid())
                    .findFirst().orElseThrow(
                        () -> new NoSubscriptionException("No main subscription found for user with id: " + id)
                    )
        );
    }

    public boolean isAdmin() {
        return getMainActiveSubscription().isAdmin(this);
    }

}
