package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.MapperException;

public enum LongTextTypeEnum {
    DESCRIPTION("description", "Description"),
    DESTINATION_DETAILS("destination_details", "Destination Details"),
    MISSION_DETAILS("mission_details", "Mission Details"),
    BENEFICIARY_ADMINISTRATION("beneficiary_administration", "Beneficiary Administration"),
    DURATION("duration", "Duration"),
    FURTHER_INFORMATION("further_information", "Further Information"),
    PROJECT_OBJECTIVE("project_objective", "Project Objective");

    final String csvName;
    final String displayName;

    LongTextTypeEnum(String csvName, String displayName) {
        this.csvName = csvName;
        this.displayName = displayName;
    }

    public String getCsvName() {
        return csvName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LongTextTypeEnum of(String name) {
        for (LongTextTypeEnum longTextTypeEnum : LongTextTypeEnum.values()) {
            if (longTextTypeEnum.getCsvName().equals(name)) {
                return longTextTypeEnum;
            }
        }
        throw new MapperException("LongTextTypeEnum not found: " + name);
    }


}
