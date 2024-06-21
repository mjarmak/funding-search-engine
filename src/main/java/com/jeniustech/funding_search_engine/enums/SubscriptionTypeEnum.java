package com.jeniustech.funding_search_engine.enums;

public enum SubscriptionTypeEnum {
    TRIAL(0),
    INDIVIDUAL(1), // 1 account
    EU_OFFICE(2), // 5 accounts
    ENTERPRISE(3); // negotiate

    private final int level;

    SubscriptionTypeEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
