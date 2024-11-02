package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import static com.jeniustech.funding_search_engine.mappers.DateMapper.toUTC;
import static com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil.removeUselessHtmlData;
import static com.jeniustech.funding_search_engine.util.StringUtil.removeQuotes;

public interface StringUtil {
    static boolean isNotEmpty(String row) {
        return row != null && !row.isEmpty() && !row.isBlank() && !row.equals("null");
    }
    static boolean isEmpty(String row) {
        return row == null || row.isEmpty() || row.isBlank() || row.equals("null");
    }
    static boolean isEmpty(Long row) {
        return row == null;
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


    static String valueOrDefault(Object value, Object defaultValue) {
        if (value == null) {
            return null;
        } else {
            return valueOrDefault(value.toString(), defaultValue);
        }
    }
    static String valueOrDefault(String value, Object defaultValue) {
        if (isNotEmpty(value)) {
            return value;
        } else {
            return (String) defaultValue;
        }
    }

    static String processString(Object str, boolean isDateString) {
        if (str == null) {
            return "";
        }
        if (isDateString) {
            return toUTC((String) str);
        }
        if (str instanceof String) {
            String out;
            out = ((String) str).trim();

            out = removeUselessHtmlData(out);
            out = ScraperStringUtil.replaceStrangeCharacters(out);

            out = ScraperStringUtil.encode(out);
            out = removeMultiSpaces(out);
            out = (out).replace("\"", "\"\"");
            if ((out).contains(",") || (out).contains("\n")) {
                out = "\"" + out + "\"";
            }

            out = nullIfEmpty(out);

            return valueOrDefault(out, "");
        }
        return str.toString();
    }
    private static String nullIfEmpty(String out) {
        if (!isNotEmpty(out)) {
            out = null;
        } else if (out.equals("-")) {
            out = null;
        }
        return out;
    }
    static String removeMultiSpaces(String out) {
        out = out.replaceAll("\\s+", " ");
        out = out.replace(" ", "");
        return out;
    }

    static String processQuery(String query) {
        if (query == null) {
            return "";
        }
        boolean exactMatch = isQuotedTwice(query);
        String queryProcessed = removeQuotes(query);
        queryProcessed = queryProcessed.replace(",", " ");
        queryProcessed = queryProcessed.replace(":", "");
        if (exactMatch) {
            queryProcessed = surroundWithQuotes(queryProcessed);
        }
        return queryProcessed;
//        if (!isQuoted(query)) {
//            return query;
//        }
//        String queryProcessed = query;
//        if (query.contains(",")) {
//            queryProcessed = query.replace(",", " AND ");
//            queryProcessed = queryProcessed.replace("AND AND", "AND");
////            queryProcessed = surroundWithQuotes(queryProcessed);
//        }
//        return queryProcessed;
    }

    static boolean isQuotedTwice(String query) {
        if (query == null || query.length() < 4) {
            return false;
        }
        return query.charAt(0) == '"'
                && query.charAt(query.length() - 1) == '"'
                && query.charAt(1) == '"'
                && query.charAt(query.length() - 2) == '"';
    }

    static String removeQuotes(String query) {
        if (query == null) {
            return "";
        }
        if (!isQuoted(query)) {
            return query;
        }
        String queryProcessed = query;
        if (query.contains("\"")) {
            queryProcessed = query.replace("\"", "");
        }
        return queryProcessed;
    }

    private static String surroundWithQuotes(String queryProcessed) {
        return "\"" + queryProcessed + "\"";
    }

    static boolean isQuoted(String queryProcessed) {
        return queryProcessed.startsWith("\"") && queryProcessed.endsWith("\"");
    }

}
