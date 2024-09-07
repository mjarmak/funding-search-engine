package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.EnumException;

import java.util.List;

public enum FrameworkProgramEnum {
    HORIZON("HORIZON"), // 0
    H2020("H2020"), // 1
    FP1("FP1"), // 2
    FP2("FP2"), // 3
    FP3("FP3"), // 4
    FP4("FP4"), // 5
    FP5("FP5"), // 6
    FP6("FP6"), // 7
    FP7("FP7"), // 8
    ;

    private final List<String> codes;

    FrameworkProgramEnum(String code) {
        this.codes = List.of(code);
    }

    public static FrameworkProgramEnum valueFrom(String value) {
        if (value == null) {
            return null;
        }
        for (FrameworkProgramEnum projectStatusEnum : FrameworkProgramEnum.values()) {
            if (projectStatusEnum.codes.contains(value)) {
                return projectStatusEnum;
            }
        }
        throw new EnumException("No Framework Program found for value: " + value);
    }

    public String getName() {
        return name();
    }

}
