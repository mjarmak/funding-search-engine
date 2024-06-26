package com.jeniustech.funding_search_engine.util;

public interface DetailFormatter {

    enum FormatTypeEnum {
        HTML, TEXT
    }

    static String format(String detail, FormatTypeEnum formatType) {
        if (detail == null) {
            return null;
        }
        if (formatType == FormatTypeEnum.HTML) {
            detail = detail.trim()
                    .replaceAll("ExpectedOutcome:", "Expected Outcome:<br>")
                    .replaceAll("\\. Scope:", ".<br>Scope:<br>");
        } else {
            detail = detail.trim()
                    .replaceAll("ExpectedOutcome:", "Expected Outcome:\n")
                    .replaceAll("\\. Scope:", ".\nScope:\n");
        }
        return detail
                .replaceAll(":(?!\\s)", ": ")
                .replaceAll(";(?!\\s)", "; ")
                .replaceAll("(?<!\\d)\\.(?!\\d)(?!\\s)", ". ")
                .replaceAll(",(?!\\s)", ", ")
                .replaceAll("\\[\\d+\\]", "")
                ;

    }
}
