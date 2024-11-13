package com.jeniustech.funding_search_engine.util;

import java.time.LocalDate;

public interface InvoiceUtil {

    static String generateInvoiceId() {
        LocalDate today = LocalDate.now();
        return generateInvoiceId(today);
    }

    static String generateInvoiceId(LocalDate today) {
        String monthValue = String.valueOf(today.getMonthValue());
        if (monthValue.length() == 1) {
            monthValue = "0" + monthValue;
        }
        String dayValue = String.valueOf(today.getDayOfMonth());
        if (dayValue.length() == 1) {
            dayValue = "0" + dayValue;
        }
        return "314." + today.getYear() + "." + monthValue + dayValue + randomBetween0And10();
    }

    static String randomBetween0And10() {
        return String.valueOf((int) (Math.random() * 10));
    }
}
