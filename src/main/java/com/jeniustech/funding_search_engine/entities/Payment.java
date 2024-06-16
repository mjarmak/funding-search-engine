package com.jeniustech.funding_search_engine.entities;

import com.jeniustech.funding_search_engine.enums.CurrencyEnum;
import com.jeniustech.funding_search_engine.enums.PaymentStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private UserSubscription subscription;

    private BigDecimal amount;

    @Enumerated(EnumType.ORDINAL)
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
