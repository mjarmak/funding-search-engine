package com.jeniustech.funding_search_engine.enums;

import static com.jeniustech.funding_search_engine.constants.SubscriptionPriceIds.*;

public enum SubscriptionTypeEnum {
    TRIAL(0, SubscriptionTypeParentEnum.TRIAL),

    INDIVIDUAL_MONTH(1, INDIVIDUAL_MONTH_PRICE_ID, SubscriptionTypeParentEnum.INDIVIDUAL, SubscriptionPeriodEnum.MONTHLY), // 1 account
    EU_OFFICE_MONTH(2, EU_OFFICE_MONTH_PRICE_ID, SubscriptionTypeParentEnum.EU_OFFICE, SubscriptionPeriodEnum.MONTHLY), // 5 accounts
    INDIVIDUAL_YEAR(1, INDIVIDUAL_YEAR_PRICE_ID, SubscriptionTypeParentEnum.INDIVIDUAL, SubscriptionPeriodEnum.YEARLY), // 1 account
    EU_OFFICE_YEAR(2, EU_OFFICE_YEAR_PRICE_ID, SubscriptionTypeParentEnum.EU_OFFICE, SubscriptionPeriodEnum.YEARLY), // 5 accounts

    ENTERPRISE(5, SubscriptionTypeParentEnum.ENTERPRISE); // negotiate

    private final int level;
    private final String priceId;
    private final SubscriptionTypeParentEnum parent;
    private final SubscriptionPeriodEnum period;

    SubscriptionTypeEnum(int level, SubscriptionTypeParentEnum parent) {
        this.level = level;
        this.priceId = null;
        this.parent = parent;
        this.period = SubscriptionPeriodEnum.MONTHLY;
    }

    SubscriptionTypeEnum(int level, String priceId, SubscriptionTypeParentEnum parent, SubscriptionPeriodEnum period) {
        this.level = level;
        this.priceId = priceId;
        this.parent = parent;
        this.period = period;
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
}
