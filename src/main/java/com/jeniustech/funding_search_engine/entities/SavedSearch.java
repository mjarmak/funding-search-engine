package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.BooleanEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "saved_search")
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    private String name;
    private String value;

    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum notification;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public void setNotification(boolean notification) {
        this.notification = BooleanEnum.fromBoolean(notification);
    }

}
