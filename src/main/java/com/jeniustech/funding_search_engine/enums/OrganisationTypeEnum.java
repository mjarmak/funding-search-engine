package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.util.StringUtil;

public enum OrganisationTypeEnum {
    PUB("Public Company"), // 0
    PRC("Private Company"), // 1
    REC("Research Institute"), // 2
    OTH("NGO"), // 3
    HES("Higher Education"), // 4
    GOV("Other"), // 5
    IND("Other"), // 6
    PRI("Other"), // 7
    R("Other"), // 8
    X("Other"), // 9
    RES("Other"), // 10
    HEC("Other"); // 11



    private final String name;

    OrganisationTypeEnum(String name) {
        this.name = name;
    }

    public static String getDisplayName(String name) {
        OrganisationTypeEnum organisationTypeEnum = of(name);
        if (organisationTypeEnum == null) {
            return null;
        }
        return organisationTypeEnum.getDisplayName();
    }

    public String getDisplayName() {
        return name;
    }

    public String getName() {
        return name();
    }

    public static OrganisationTypeEnum of(String name) {
        if (!StringUtil.isNotEmpty(name)) {
            return null;
        }
        return valueOf(name.toUpperCase());
    }
}
