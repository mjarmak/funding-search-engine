package com.jeniustech.funding_search_engine.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallTest {

    @Test
    void getBudgetStringTest() {
        Call call = getCall("1000.50", "1000000");
        assertEquals("1000.5", call.getBudgetMinString());
        assertEquals("1000000", call.getBudgetMaxString());

        call = getCall("867", "27862872");
        assertEquals("867", call.getBudgetMinString());
        assertEquals("27862872", call.getBudgetMaxString());

        call = getCall("0.01", "0");
        assertEquals("0.01", call.getBudgetMinString());
        assertEquals("0", call.getBudgetMaxString());

        call = getCall("0", "0");
        assertEquals("0", call.getBudgetMinString());
        assertEquals("0", call.getBudgetMaxString());

        call = getCall("100000000000000", "86198249841288927");
        assertEquals("100000000000000", call.getBudgetMinString());
        assertEquals("86198249841288927", call.getBudgetMaxString());

        call = getCall("100000000000000.0000000001", "86198249841288927.0000000001");
        assertEquals("100000000000000.0000000001", call.getBudgetMinString());
        assertEquals("86198249841288927.0000000001", call.getBudgetMaxString());
    }

    private static Call getCall(String min, String max) {
        return Call.builder()
                .budgetMin(new BigDecimal(min))
                .budgetMax(new BigDecimal(max))
                .build();
    }

}
