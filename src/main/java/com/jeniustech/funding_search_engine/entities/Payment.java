package com.jeniustech.funding_search_engine.entities;


import com.jeniustech.funding_search_engine.mappers.DateMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    private String invoiceId;
    private String stripePaymentId;

    private BigDecimal amount;

    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private BusinessInformation businessInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserData userData;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public String getCurrencyDisplay() {
        if (currency == null) {
            currency = "EUR";
        } else {
            currency = currency.toUpperCase();
        }
        return currency;
    }

    public Timestamp getCreatedAt() {
        if (createdAt == null) {
            createdAt = DateMapper.map(LocalDateTime.now());
        }
        return createdAt;
    }

    public String getCreatedAtDisplay() {
        return DateMapper.formatToDisplay(DateMapper.map(this.getCreatedAt()));
    }

    public String getCreatedAtDisplayDate() {
        return DateMapper.formatToDisplay(DateMapper.map(this.getCreatedAt()).toLocalDate());
    }

    public BigDecimal getAmountExcludingVAT(BigDecimal vat) {
        BigDecimal value121 = getVATDivide(vat);
        return amount.divide(value121, 2, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    BigDecimal getVATDivide(BigDecimal vat) {
        return vat.add(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getVAT(BigDecimal vat) {
        return amount.subtract(getAmountExcludingVAT(vat)).stripTrailingZeros();
    }

    public String getCommunicationMessage() {
        if (getInvoiceId() == null) {
            return null;
        }
        return "+++" + getInvoiceId().replace(".", "/") + "+++";
    }
}
