package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.enums.LongTextTypeEnum;
import com.jeniustech.funding_search_engine.enums.SubmissionProcedureEnum;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface StringUtil {
    static boolean isNotEmpty(String row) {
        return row != null && !row.isEmpty() && !row.isBlank() && !row.equals("null");
    }

    static boolean isNotEmpty(Short row) {
        return row != null;
    }

    static boolean isNotEmpty(BigDecimal row) {
        return row != null;
    }

    static boolean isNotEmpty(SubmissionProcedureEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(UrlTypeEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(LongTextTypeEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(Timestamp row) {
        return row != null;
    }


    static String valueOrDefault(String value, Object defaultValue) {
        if (isNotEmpty(value)) {
            return value;
        } else {
            return (String) defaultValue;
        }
    }
}
