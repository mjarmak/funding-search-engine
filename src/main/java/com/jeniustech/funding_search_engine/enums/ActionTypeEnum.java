package com.jeniustech.funding_search_engine.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ActionTypeEnum {
    UNKNOWN,
    RIA,
    CSA,
    COFUND,
    IA,
    PCP,
    TMA,
    FPA;

    public static ActionTypeEnum of(String actionType) {
        if (actionType == null) {
            return UNKNOWN;
        }
        try {
            return ActionTypeEnum.valueOf(actionType);
        } catch (IllegalArgumentException e) {
            log.error("Invalid action type: {}", actionType);
            return UNKNOWN;
        }
    }

    public String getName() {
        return this.name();
    }
}


