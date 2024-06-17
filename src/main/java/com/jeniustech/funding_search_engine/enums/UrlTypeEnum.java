package com.jeniustech.funding_search_engine.enums;

public enum UrlTypeEnum {
    TOPIC_DETAILS,
    PROSPECT,
    COMPETITIVE_CALL;

    public String getUrl(String identifier, String urlId) {
        return switch (this) {
            case TOPIC_DETAILS -> "https://ec.europa.eu/info/funding-tenders/opportunities/portal/screen/opportunities/topic-details/" + identifier;
            case PROSPECT -> "https://ec.europa.eu/info/funding-tenders/opportunities/portal/screen/opportunities/prospect-details/" + urlId;
            case COMPETITIVE_CALL -> "https://ec.europa.eu/info/funding-tenders/opportunities/portal/screen/opportunities/competitive-calls-cs/" + urlId;
            default -> null;
        };
    }

    public static UrlTypeEnum getType(String reference) {
        if (reference.contains("COMPETITIVE_CALL")) {
            return COMPETITIVE_CALL;
        } else if (reference.contains("PROSPECTSEN")) {
            return PROSPECT;
        } else {
            return TOPIC_DETAILS;
        }
    }

}
