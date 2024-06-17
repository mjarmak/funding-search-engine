package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.MapperException;

public enum LongTextTypeEnum {
    DESCRIPTION("description"),
    DESTINATION_DETAILS("destination_details"),
    MISSION_DETAILS("mission_details"),
    BENEFICIARY_ADMINISTRATION("beneficiary_administration"),
    DURATION("duration"),
    FURTHER_INFORMATION("further_information");

    String name;

    LongTextTypeEnum(String name) {
        this.name = this.name();
    }

    public String getName() {
        return name;
    }

    public LongTextTypeEnum of(String name) {
        for (LongTextTypeEnum longTextTypeEnum : LongTextTypeEnum.values()) {
            if (longTextTypeEnum.getName().equals(name)) {
                return longTextTypeEnum;
            }
        }
        throw new MapperException("LongTextTypeEnum not found: " + name);
    }


}
