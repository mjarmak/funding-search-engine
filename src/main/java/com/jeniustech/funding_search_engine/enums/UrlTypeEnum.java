package com.jeniustech.funding_search_engine.enums;

public enum UrlTypeEnum {
    EU,
    PROSPECT,
    COMPETITIVE_CALL;

    public String getUrl(String urlId) {
        return switch (this) {
            case EU -> "https://eu-url/" + urlId;
            case PROSPECT -> "https://prospect-url/" + urlId;
            case COMPETITIVE_CALL -> "https://competitive-calls-url/" + urlId;
            default -> null;
        };
    }
}
