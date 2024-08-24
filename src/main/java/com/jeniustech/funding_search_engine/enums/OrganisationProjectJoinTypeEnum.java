package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.util.StringUtil;

public enum OrganisationProjectJoinTypeEnum {
    PARTICIPANT("participant", 3),
    COORDINATOR("coordinator", 2),
    THIRD_PARTY("thirdParty", 4),
    ASSOCIATED_PARTNER("associatedPartner", 1),
    UNKNOWN(null, 99);

    private final String name;
    private final int hierarchy;


    public int getHierarchy() {
        return hierarchy;
    }
    OrganisationProjectJoinTypeEnum(final String name, final int hierarchy) {
        this.name = name;
        this.hierarchy = hierarchy;
    }

    public static OrganisationProjectJoinTypeEnum valueOfName(String name) {
        if (!StringUtil.isNotEmpty(name)) {
            return null;
        }
        for (OrganisationProjectJoinTypeEnum organisationProjectJoinTypeEnum : OrganisationProjectJoinTypeEnum.values()) {
            if (organisationProjectJoinTypeEnum.name.equals(name)) {
                return organisationProjectJoinTypeEnum;
            }
        }
        return null;
    }
}
