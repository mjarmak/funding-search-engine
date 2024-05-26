package com.jeniustech.funding_search_engine.enums;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ActionTypeEnum {
    NA,
    RIA,
    CSA,
    COFUND,
    IA,
    PCP,
    TMA,
    FPA;

    public static ActionTypeEnum of(String actionType) {
        if (actionType == null) {
            return NA;
        }
        try {
            return ActionTypeEnum.valueOf(actionType);
        } catch (IllegalArgumentException e) {
            log.error("Invalid action type: {}", actionType);
            return NA;
        }
    }
}


