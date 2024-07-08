package com.jeniustech.funding_search_engine.enums;

public enum BooleanEnum {
    FALSE,
    TRUE;

    public static BooleanEnum fromBoolean(String value) {
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value.toLowerCase()) ? TRUE : FALSE;
    }
}
