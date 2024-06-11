package com.jeniustech.funding_search_engine.enums;

import com.jeniustech.funding_search_engine.exceptions.MapperException;

public enum SubmissionProcedureEnum {

    SINGLE_STAGE("single-stage"),
    TWO_STAGE("two-stage"),
    MULTIPLE_CUTOFF("multiple cut-off");

    private final String value;

    SubmissionProcedureEnum(String value) {
        this.value = value;
    }

    public static SubmissionProcedureEnum of(String value) {
        if (value == null || value.isEmpty()) {
            return SINGLE_STAGE;
        }
        for (SubmissionProcedureEnum submissionProcedureEnum : SubmissionProcedureEnum.values()) {
            if (submissionProcedureEnum.value.equals(value)) {
                return submissionProcedureEnum;
            }
        }
        throw new MapperException("Invalid submission procedure: " + value);
    }

}
