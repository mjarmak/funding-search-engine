package com.jeniustech.funding_search_engine.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvoiceUtilTest {

    @Test
    void testGenerateInvoiceId() {
        String result = InvoiceUtil.generateInvoiceId();

        assertEquals(14, result.length());
        assertEquals("314", result.split("\\.")[0]);

        LocalDate today = LocalDate.now();
        assertEquals(String.valueOf(today.getYear()), result.split("\\.")[1]);
        assertEquals(today.getMonthValue() + String.valueOf(today.getDayOfMonth()), result.split("\\.")[2].substring(0, 4));
        assertEquals(5, result.split("\\.")[2].length());
    }

    @Test
    void testGenerateInvoiceId_manualDate_1() {
        LocalDate today = LocalDate.of(2021, 1, 1);
        String result = InvoiceUtil.generateInvoiceId(today);

        System.out.println(result);

        assertEquals(14, result.length());
        assertEquals("314", result.split("\\.")[0]);
        assertEquals("2021", result.split("\\.")[1]);
        assertEquals("0101", result.split("\\.")[2].substring(0, 4));
        assertEquals(5, result.split("\\.")[2].length());
    }

    @Test
    void testGenerateInvoiceId_manualDate_2() {
        LocalDate today = LocalDate.of(2021, 12, 12);
        String result = InvoiceUtil.generateInvoiceId(today);

        System.out.println(result);

        assertEquals(14, result.length());
        assertEquals("314", result.split("\\.")[0]);
        assertEquals("2021", result.split("\\.")[1]);
        assertEquals("1212", result.split("\\.")[2].substring(0, 4));
        assertEquals(5, result.split("\\.")[2].length());
    }

    @Test
    void testGenerateInvoiceId_manualDate_3() {
        LocalDate today = LocalDate.of(2021, 5, 12);
        String result = InvoiceUtil.generateInvoiceId(today);

        System.out.println(result);

        assertEquals(14, result.length());
        assertEquals("314", result.split("\\.")[0]);
        assertEquals("2021", result.split("\\.")[1]);
        assertEquals("0512", result.split("\\.")[2].substring(0, 4));
        assertEquals(5, result.split("\\.")[2].length());
    }

    @Test
    void testGenerateInvoiceId_manualDate_4() {
        LocalDate today = LocalDate.of(2021, 12, 5);
        String result = InvoiceUtil.generateInvoiceId(today);

        System.out.println(result);

        assertEquals(14, result.length());
        assertEquals("314", result.split("\\.")[0]);
        assertEquals("2021", result.split("\\.")[1]);
        assertEquals("1205", result.split("\\.")[2].substring(0, 4));
        assertEquals(5, result.split("\\.")[2].length());
    }

    @Test
    void randomBetween0And10() {
        Stream.iterate(0, i -> i + 1)
                .limit(1000)
                .forEach(i -> {
                            int result = Integer.parseInt(InvoiceUtil.randomBetween0And10());
                            System.out.println(result);
                            assertTrue(result >= 0 && result <= 9);
                        }
                );
    }

}
