package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.EnumException;

public enum ProjectStatusEnum {
    SIGNED("Signed"),
    TERMINATED("Terminated"),
    CLOSED("Closed");

    private final String name;

    ProjectStatusEnum(String name) {
        this.name = name;
    }

    public static ProjectStatusEnum valueFrom(String value) {
        try {
            return ProjectStatusEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EnumException("Invalid project status: " + value);
        }
    }

    public String getName() {
        return name;
    }

}
