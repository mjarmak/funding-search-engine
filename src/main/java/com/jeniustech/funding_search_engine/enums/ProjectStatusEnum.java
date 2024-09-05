package com.jeniustech.funding_search_engine.enums;

import java.util.List;

public enum ProjectStatusEnum {
    SIGNED("Signed", List.of("SIGNED", "ONG")),
    TERMINATED("Terminated", List.of("TERMINATED")),
    CLOSED("Closed", List.of("CLOSED", "CLO"));

    private final String name;
    private final List<String> codes;

    ProjectStatusEnum(String name, List<String> codes) {
        this.name = name;
        this.codes = codes;
    }

    public static ProjectStatusEnum valueFrom(String value) {
        try {
            for (ProjectStatusEnum projectStatusEnum : ProjectStatusEnum.values()) {
                if (projectStatusEnum.codes.contains(value)) {
                    return projectStatusEnum;
                }
            }
        } catch (IllegalArgumentException e) {
            return CLOSED;
        }
        return CLOSED;
    }

    public String getName() {
        return name;
    }

}
