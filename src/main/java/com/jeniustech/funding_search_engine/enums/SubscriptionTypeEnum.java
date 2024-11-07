package com.jeniustech.funding_search_engine.enums;

import static com.jeniustech.funding_search_engine.constants.SubscriptionPriceIds.*;

public enum SubscriptionTypeEnum {
    TRIAL(0, SubscriptionTypeParentEnum.TRIAL, "Trial"),

    INDIVIDUAL_MONTH(1, INDIVIDUAL_MONTH_PRICE_ID, SubscriptionTypeParentEnum.INDIVIDUAL, SubscriptionPeriodEnum.MONTHLY, "Individual (Monthly)"), // 1 account
    EU_OFFICE_MONTH(2, EU_OFFICE_MONTH_PRICE_ID, SubscriptionTypeParentEnum.EU_OFFICE, SubscriptionPeriodEnum.MONTHLY, "EU Office (Monthly)"), // 5 accounts
    INDIVIDUAL_YEAR(1, INDIVIDUAL_YEAR_PRICE_ID, SubscriptionTypeParentEnum.INDIVIDUAL, SubscriptionPeriodEnum.YEARLY, "Individual (Yearly)"), // 1 account
    EU_OFFICE_YEAR(2, EU_OFFICE_YEAR_PRICE_ID, SubscriptionTypeParentEnum.EU_OFFICE, SubscriptionPeriodEnum.YEARLY, "EU Office (Yearly)"), // 5 accounts

    ENTERPRISE(5, SubscriptionTypeParentEnum.ENTERPRISE, "Enterprise"); // negotiate

    private final int level;
    private final String priceId;
    private final SubscriptionTypeParentEnum parent;
    private final SubscriptionPeriodEnum period;
    private final String displayName;

    SubscriptionTypeEnum(int level, SubscriptionTypeParentEnum parent, String displayName) {
        this.level = level;
        this.priceId = null;
        this.parent = parent;
        this.period = SubscriptionPeriodEnum.MONTHLY;
        this.displayName = displayName;
    }

    SubscriptionTypeEnum(int level, String priceId, SubscriptionTypeParentEnum parent, SubscriptionPeriodEnum period, String displayName) {
        this.level = level;
        this.priceId = priceId;
        this.parent = parent;
        this.period = period;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getPriceId() {
        return priceId;
    }

    public SubscriptionTypeParentEnum getParent() {
        return parent;
    }

    public SubscriptionPeriodEnum getPeriod() {
        return period;
    }

    public String getDisplayName() {
        return displayName;
    }
}
