package com.jeniustech.funding_search_engine.util;

public interface DetailFormatter {
    static String format(String detail) {
        if (detail == null) {
            return null;
        }
        return detail.trim()
//                .replaceAll("ExpectedOutcome:", "<b>Expected Outcome:</b><br>")
                .replaceAll("ExpectedOutcome:", "Expected Outcome:<br>")
                .replaceAll("\\. Scope:", "\\.<br>Scope:<br>")
                .replaceAll(":(?!\\s)", ": ")
                .replaceAll(";(?!\\s)", "; ")
                .replaceAll("(?<!\\d)\\.(?!\\d)(?!\\s)", ". ")
                .replaceAll(",(?!\\s)", ", ")
                .replaceAll("\\[\\d+\\]", "")
                ;

    }
}
