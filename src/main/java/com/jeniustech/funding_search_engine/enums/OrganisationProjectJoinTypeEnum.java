package com.jeniustech.funding_search_engine.enums;

public enum OrganisationProjectJoinTypeEnum {
    PARTICIPANT("participant", 3),
    COORDINATOR("coordinator", 2),
    THIRD_PARTY("thirdParty", 4),
    ASSOCIATED_PARTNER("associatedPartner", 1);

    private final String name;
    private final int hierarchy;


    public int getHierarchy() {
        return hierarchy;
    }
    OrganisationProjectJoinTypeEnum(final String name, final int hierarchy) {
        this.name = name;
        this.hierarchy = hierarchy;
    }

    public static OrganisationProjectJoinTypeEnum valueOfName(String stringCellValue) {
        for (OrganisationProjectJoinTypeEnum organisationProjectJoinTypeEnum : OrganisationProjectJoinTypeEnum.values()) {
            if (organisationProjectJoinTypeEnum.name.equals(stringCellValue)) {
                return organisationProjectJoinTypeEnum;
            }
        }
        return null;
    }
}
