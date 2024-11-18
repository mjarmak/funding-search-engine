package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.util.StringUtil;

public enum OrganisationProjectJoinTypeEnum {
    PARTICIPANT("participant", 3),
    COORDINATOR("coordinator", 2),
    THIRD_PARTY("thirdParty", 4),
    ASSOCIATED_PARTNER("associatedPartner", 1),
    UNKNOWN("", -1);

    private final String csvName;
    private final int hierarchy;


    public int getHierarchy() {
        return hierarchy;
    }
    OrganisationProjectJoinTypeEnum(final String csvName, final int hierarchy) {
        this.csvName = csvName;
        this.hierarchy = hierarchy;
    }

    public static OrganisationProjectJoinTypeEnum valueOfName(String csvName) {
        if (!StringUtil.isNotEmpty(csvName)) {
            return null;
        }
        for (OrganisationProjectJoinTypeEnum organisationProjectJoinTypeEnum : OrganisationProjectJoinTypeEnum.values()) {
            if (organisationProjectJoinTypeEnum.csvName.equals(csvName)) {
                return organisationProjectJoinTypeEnum;
            }
        }
        return null;
    }

    public String getName() {
        return name();
    }
}
