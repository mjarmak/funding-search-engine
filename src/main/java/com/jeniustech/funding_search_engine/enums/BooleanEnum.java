package com.jeniustech.funding_search_engine.enums;

public enum BooleanEnum {
    FALSE,
    TRUE;

    public static BooleanEnum fromBoolean(Boolean value) {
        return value ? TRUE : FALSE;
    }
}
