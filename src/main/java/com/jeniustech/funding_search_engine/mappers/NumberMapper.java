package com.jeniustech.funding_search_engine.mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public interface NumberMapper {

    static String shortenNumber(String number) {
        if (number == null) {
            return null;
        }
        return shortenNumber(new BigDecimal(number), 1);
    }

    static String shortenNumber(String number, int decimalPoints) {
        if (number == null) {
            return null;
        }
        return shortenNumber(new BigDecimal(number), decimalPoints);
    }
    static String shortenNumber(BigDecimal number, int decimalPoints) {
        if (number == null) {
            return null;
        }
        // check if negative number
        if (number.compareTo(BigDecimal.ZERO) < 0) {
            return "-" + shortenNumber(number.negate(), decimalPoints);
        }
        BigDecimal thousand = new BigDecimal(1000);
        BigDecimal million = new BigDecimal(1000000);
        BigDecimal billion = new BigDecimal(1000000000);
        BigDecimal trillion = new BigDecimal(1000000000000L);

        if (number.compareTo(thousand) < 0) {
            return number.toString(); // No formatting needed
        }

        BigDecimal value;
        String suffix;

        if (number.compareTo(thousand) >= 0 && number.compareTo(million) < 0) {
            value = number.divide(thousand, decimalPoints, RoundingMode.HALF_UP);
            suffix = "K";
        } else if (number.compareTo(million) >= 0 && number.compareTo(billion) < 0) {
            value = number.divide(million, decimalPoints, RoundingMode.HALF_UP);
            suffix = "M";
        } else if (number.compareTo(billion) >= 0 && number.compareTo(trillion) < 0) {
            value = number.divide(billion, decimalPoints, RoundingMode.HALF_UP);
            suffix = "B";
        } else {
            value = number.divide(trillion, decimalPoints, RoundingMode.HALF_UP);
            suffix = "T";
        }

        value = value.stripTrailingZeros();
        return value.toPlainString() + suffix;
    }

    static String formatNumberWithCommas(String number) {
        if (number == null) {
            return null;
        }
        return formatNumberWithCommas(new BigDecimal(number));
    }

    static String formatNumberWithCommas(BigDecimal number) {
        if (number == null) {
            return null;
        }
        return new DecimalFormat("#,##0.#####").format(number);
    }

}
