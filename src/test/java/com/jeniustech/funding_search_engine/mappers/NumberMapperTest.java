package com.jeniustech.funding_search_engine.mappers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberMapperTest {

    @Test
    public void testNumbersBelowThousand() {
        assertEquals("999", NumberMapper.shortenNumber(new BigDecimal("999"), 2));
        assertEquals("1", NumberMapper.shortenNumber(new BigDecimal("1"), 2));
        assertEquals("999.99", NumberMapper.shortenNumber(new BigDecimal("999.99"), 2));

        assertEquals("999", NumberMapper.shortenNumber(new BigDecimal("999"), 1));
        assertEquals("1", NumberMapper.shortenNumber(new BigDecimal("1"), 1));
        assertEquals("999.99", NumberMapper.shortenNumber(new BigDecimal("999.99"), 1));
    }

    @Test
    public void testNumbersInThousands() {
        assertEquals("1K", NumberMapper.shortenNumber(new BigDecimal("1000"), 2));
        assertEquals("1.5K", NumberMapper.shortenNumber(new BigDecimal("1500"), 2));
        assertEquals("10K", NumberMapper.shortenNumber(new BigDecimal("10000"), 2));
        assertEquals("999.9K", NumberMapper.shortenNumber(new BigDecimal("999900"), 2));
    }

    @Test
    public void testNumbersInMillions() {
        assertEquals("1M", NumberMapper.shortenNumber(new BigDecimal("1000000"), 2));
        assertEquals("2.5M", NumberMapper.shortenNumber(new BigDecimal("2500000"), 2));
        assertEquals("10M", NumberMapper.shortenNumber(new BigDecimal("10000000"), 2));
        assertEquals("987.65M", NumberMapper.shortenNumber(new BigDecimal("987654321"), 2));
        assertEquals("987.7M", NumberMapper.shortenNumber(new BigDecimal("987654321"), 1));
    }

    @Test
    public void testNumbersInBillions() {
        assertEquals("1B", NumberMapper.shortenNumber(new BigDecimal("1000000000"), 2));
        assertEquals("2.5B", NumberMapper.shortenNumber(new BigDecimal("2500000000"), 2));
        assertEquals("10B", NumberMapper.shortenNumber(new BigDecimal("10000000000"), 2));
    }

    @Test
    public void testNumbersInTrillions() {
        assertEquals("1T", NumberMapper.shortenNumber(new BigDecimal("1000000000000"), 2));
        assertEquals("2.5T", NumberMapper.shortenNumber(new BigDecimal("2500000000000"), 2));
        assertEquals("10T", NumberMapper.shortenNumber(new BigDecimal("10000000000000"), 2));
    }

    @Test
    public void testDecimalNumbers() {
        assertEquals("1.2K", NumberMapper.shortenNumber(new BigDecimal("1200"), 2));
        assertEquals("1.23K", NumberMapper.shortenNumber(new BigDecimal("1230"), 2));
        assertEquals("1.23K", NumberMapper.shortenNumber(new BigDecimal("1234"), 2));
        assertEquals("1.23K", NumberMapper.shortenNumber(new BigDecimal("1234.5"), 2));

        assertEquals("1.2K", NumberMapper.shortenNumber(new BigDecimal("1200"), 1));
        assertEquals("1.2K", NumberMapper.shortenNumber(new BigDecimal("1230"), 1));
        assertEquals("1.2K", NumberMapper.shortenNumber(new BigDecimal("1234"), 1));
        assertEquals("1.2K", NumberMapper.shortenNumber(new BigDecimal("1234.5"), 1));
    }

    @Test
    public void testZero() {
        assertEquals("0", NumberMapper.shortenNumber(new BigDecimal("0"), 2));
    }

    @Test
    public void testNegativeNumbers() {
        assertEquals("-999", NumberMapper.shortenNumber(new BigDecimal("-999"), 2));
        assertEquals("-1", NumberMapper.shortenNumber(new BigDecimal("-1"), 2));
        assertEquals("-999.99", NumberMapper.shortenNumber(new BigDecimal("-999.99"), 2));
        assertEquals("-1K", NumberMapper.shortenNumber(new BigDecimal("-1000"), 2));
        assertEquals("-1.5K", NumberMapper.shortenNumber(new BigDecimal("-1500"), 2));
        assertEquals("-10K", NumberMapper.shortenNumber(new BigDecimal("-10000"), 2));
        assertEquals("-999.9K", NumberMapper.shortenNumber(new BigDecimal("-999900"), 2));

        assertEquals("-1M", NumberMapper.shortenNumber(new BigDecimal("-1000000"), 2));
        assertEquals("-2.5M", NumberMapper.shortenNumber(new BigDecimal("-2500000"), 2));
        assertEquals("-10M", NumberMapper.shortenNumber(new BigDecimal("-10000000"), 2));
        assertEquals("-987.65M", NumberMapper.shortenNumber(new BigDecimal("-987654321"), 2));

        assertEquals("-1K", NumberMapper.shortenNumber(new BigDecimal("-1000"), 2));
        assertEquals("-1.5K", NumberMapper.shortenNumber(new BigDecimal("-1500"), 2));
        assertEquals("-1M", NumberMapper.shortenNumber(new BigDecimal("-1000000"), 2));

        assertEquals("-1.2K", NumberMapper.shortenNumber(new BigDecimal("-1200"), 2));
        assertEquals("-1.23K", NumberMapper.shortenNumber(new BigDecimal("-1230"), 2));
        assertEquals("-1.23K", NumberMapper.shortenNumber(new BigDecimal("-1234"), 2));
        assertEquals("-1.23K", NumberMapper.shortenNumber(new BigDecimal("-1234.5"), 2));

        assertEquals("-1T", NumberMapper.shortenNumber(new BigDecimal("-1000000000000"), 2));
        assertEquals("-2.5T", NumberMapper.shortenNumber(new BigDecimal("-2500000000000"), 2));
        assertEquals("-10T", NumberMapper.shortenNumber(new BigDecimal("-10000000000000"), 2));
    }

    @Test
    public void formatNumberWithCommas() {
        assertEquals("1,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000")));
        assertEquals("1,000,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000")));
        assertEquals("1,000,000,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000")));
        assertEquals("1,000,000,000,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000000")));

        assertEquals("1,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000.00")));
        assertEquals("1,000,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000.00")));
        assertEquals("1,000,000,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000.00")));
        assertEquals("1,000,000,000,000", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000000.00")));

        assertEquals("1,000", NumberMapper.formatNumberWithCommas("1000"));
        assertEquals("1,000,000", NumberMapper.formatNumberWithCommas("1000000"));
        assertEquals("1,000,000,000", NumberMapper.formatNumberWithCommas("1000000000"));
        assertEquals("1,000,000,000,000", NumberMapper.formatNumberWithCommas("1000000000000"));

        assertEquals("1,000", NumberMapper.formatNumberWithCommas("1000.00"));
        assertEquals("1,000,000", NumberMapper.formatNumberWithCommas("1000000.00"));
        assertEquals("1,000,000,000", NumberMapper.formatNumberWithCommas("1000000000.00"));
        assertEquals("1,000,000,000,000", NumberMapper.formatNumberWithCommas("1000000000000.00"));

        assertEquals("1,000.5", NumberMapper.formatNumberWithCommas(new BigDecimal("1000.50")));
        assertEquals("1,000,000.05", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000.05")));
        assertEquals("1,000,000,000.7", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000.70")));
        assertEquals("1,000,000,000,000.99", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000000.99")));
        assertEquals("1,000,000,000,000.99999", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000000.99999")));
        assertEquals("1,000,000,000,001", NumberMapper.formatNumberWithCommas(new BigDecimal("1000000000000.999999")));

        assertEquals("1,000.5", NumberMapper.formatNumberWithCommas("1000.50"));
        assertEquals("1,000,000.05", NumberMapper.formatNumberWithCommas("1000000.05"));
        assertEquals("1,000,000,000.7", NumberMapper.formatNumberWithCommas("1000000000.70"));
        assertEquals("1,000,000,000,000.99", NumberMapper.formatNumberWithCommas("1000000000000.99"));
        assertEquals("1,000,000,000,000.99999", NumberMapper.formatNumberWithCommas("1000000000000.99999"));
        assertEquals("1,000,000,000,001", NumberMapper.formatNumberWithCommas("1000000000000.999999"));
    }

}
