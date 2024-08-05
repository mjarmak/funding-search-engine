package com.jeniustech.funding_search_engine.enums;

public enum ProjectStatusEnum {
    SIGNED("Signed"),
    TERMINATED("Terminated"),
    CLOSED("Closed");

    private final String name;

    ProjectStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
