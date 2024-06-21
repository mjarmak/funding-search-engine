package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.exceptions.SubscriptionPlanException;

public class ValidatorService {

    public static void validateUserSearch(UserData userData) {
        if (!userData.hasActiveSubscription()) {
            throw new SubscriptionPlanException("User has no active subscription");
        }
    }

    public static void validateUserFavorite(UserSubscription subscription, long favoritesCount) {
        if (subscription.isTrial() && favoritesCount >= 3) {
            throw new SubscriptionPlanException("You can't have more than 3 favorite calls with a trial subscription");
        }
    }

    public static void validateUserExcelExport(UserData userData, Long exportCount) {
        if (userData.getMainActiveSubscription().isTrial() && exportCount >= 10) {
            throw new SubscriptionPlanException("Trial user has reached the limit of Excel 10 exports");
        }
    }
}
