package com.jeniustech.funding_search_engine.entities;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentTest {

    public static final BigDecimal VAT = new BigDecimal(21);

    @Test
    void getVATDivide() {
        assertEquals("1.21", Payment.builder().build().getVATDivide(VAT).toString());
    }

    @Test
    void getAmountExcludingVAT_1() {
        Payment payment = Payment.builder()
                .amount(new BigDecimal(100))
                .build();

        assertEquals("82.64", payment.getAmountExcludingVAT(VAT).toString());
        assertEquals("17.36", payment.getVAT(VAT).toString());
    }

    @Test
    void getAmountExcludingVAT_2() {
        Payment payment = Payment.builder()
                .amount(new BigDecimal("301.29"))
                .build();

        assertEquals("249", payment.getAmountExcludingVAT(VAT).toString());
        assertEquals("52.29", payment.getVAT(VAT).toString());
    }

    @Test
    void getCommunicationMessage() {
        Payment payment = Payment.builder()
                .invoiceId("314.2024.11111")
                .build();

        assertEquals("+++314/2024/11111+++", payment.getCommunicationMessage());
    }
}
