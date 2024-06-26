package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.MapperException;

public enum LongTextTypeEnum {
    DESCRIPTION("description", "Description"),
    DESTINATION_DETAILS("destination_details", "Destination Details"),
    MISSION_DETAILS("mission_details", "Mission Details"),
    BENEFICIARY_ADMINISTRATION("beneficiary_administration", "Beneficiary Administration"),
    DURATION("duration", "Duration"),
    FURTHER_INFORMATION("further_information", "Further Information");

    final String name;
    final String displayName;

    LongTextTypeEnum(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
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
