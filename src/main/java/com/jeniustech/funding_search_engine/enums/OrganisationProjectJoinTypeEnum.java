package com.jeniustech.funding_search_engine.enums;

public enum OrganisationProjectJoinTypeEnum {
    PARTICIPANT("participant"),
    COORDINATOR("coordinator"),
    THIRD_PARTY("thirdParty"),
    ASSOCIATED_PARTNER("associatedPartner");

    private final String name;

    OrganisationProjectJoinTypeEnum(final String name) {
        this.name = name;
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
