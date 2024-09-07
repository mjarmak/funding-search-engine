package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.EnumException;

import java.util.List;

public enum FrameworkProgramEnum {
    HORIZON("HORIZON"),
    H2020("H2020"),
    FP1("FP1"),
    FP2("FP1"),
    FP3("FP1"),
    FP4("FP1"),
    FP5("FP1"),
    FP6("FP1"),
    FP7("FP1"),
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
