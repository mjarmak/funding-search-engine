package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.CurrencyEnum;
import com.jeniustech.funding_search_engine.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@Entity
@RequiredArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private UserSubscription subscription;

    private BigDecimal amount;
    private CurrencyEnum currency;

    private Timestamp startDate;
    private Timestamp endDate;

    private PaymentStatusEnum status;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
