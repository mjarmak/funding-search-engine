package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.util.StringUtil;

public enum OrganisationTypeEnum {
    PUB,
    PRC,
    REC,
    OTH,
    HES;

    public static OrganisationTypeEnum of(String stringCellValue) {
        if (!StringUtil.isNotEmpty(stringCellValue)) {
            return null;
        }
        return valueOf(stringCellValue.toUpperCase());
    }
}
