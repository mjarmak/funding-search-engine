package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.util.StringUtil;

public enum OrganisationTypeEnum {
    PUB("Public Company"),
    PRC("Private Company"),
    REC("Research Institute"),
    OTH("NGO"),
    HES("Higher Education");

    private final String name;

    OrganisationTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OrganisationTypeEnum of(String stringCellValue) {
        if (!StringUtil.isNotEmpty(stringCellValue)) {
            return null;
        }
        return valueOf(stringCellValue.toUpperCase());
    }
}
