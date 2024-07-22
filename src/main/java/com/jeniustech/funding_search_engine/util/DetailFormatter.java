package com.jeniustech.funding_search_engine.util;

public interface DetailFormatter {

    enum FormatTypeEnum {
        HTML, TEXT
    }

    static String format(String detail, FormatTypeEnum formatType) {
        if (detail == null) {
            return null;
        }
        return detail;
    }
}
