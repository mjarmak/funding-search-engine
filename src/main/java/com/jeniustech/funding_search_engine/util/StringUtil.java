package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.enums.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

public interface StringUtil {
    static boolean isNotEmpty(String row) {
        return row != null && !row.isEmpty() && !row.isBlank() && !row.equals("null");
    }
    static boolean isEmpty(String row) {
        return row == null || row.isEmpty() || row.isBlank() || row.equals("null");
    }

    static boolean isNotEmpty(Long row) {
        return row != null;
    }

    static boolean isNotEmpty(LocalDate row) {
        return row != null;
    }

    static boolean isNotEmpty(CountryEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(Call row) {
        return row != null;
    }

    static boolean isNotEmpty(Short row) {
        return row != null;
    }

    static boolean isNotEmpty(BigDecimal row) {
        return row != null && row.compareTo(BigDecimal.ZERO) != 0;
    }

    static boolean isNotEmpty(SubmissionProcedureEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(UrlTypeEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(BooleanEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(OrganisationTypeEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(ProjectStatusEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(LongTextTypeEnum row) {
        return row != null;
    }

    static boolean isNotEmpty(Timestamp row) {
        return row != null;
    }

    static boolean isNotEmpty(FundingSchemeEnum row) {
        return row != null;
    }


    static String valueOrDefault(String value, Object defaultValue) {
        if (isNotEmpty(value)) {
            return value;
        } else {
            return (String) defaultValue;
        }
    }
    static String removeMultiSpaces(String out) {
        out = out.replaceAll("\\s+", " ");
        out = out.replace(" ", "");
        return out;
    }

}
