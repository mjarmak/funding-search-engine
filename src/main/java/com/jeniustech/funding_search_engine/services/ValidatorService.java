package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.NotAllowedActionException;
import com.jeniustech.funding_search_engine.exceptions.SubscriptionPlanException;

import javax.ws.rs.ForbiddenException;

public class ValidatorService {

    public static void validateUserIsAdmin(UserData adminUserData, UserSubscription subscription) throws ForbiddenException {

        if (!subscription.isAdmin(adminUserData)) {
            throw new ForbiddenException("User is not admin of subscription");
        }
    }

    public static void validateSubscriptionType(UserData userData, SubscriptionTypeEnum type) {
        if (type == null) {
            throw new NotAllowedActionException("Subscription type is not valid");
        } else if (type.equals(SubscriptionTypeEnum.TRIAL)) {
            throw new NotAllowedActionException("Trial subscription can't be created");
        }
        UserSubscription subscription = userData.getMainSubscription();
        if (!subscription.isActive()) {
            return;
        }
        if (subscription.getType().getParent().equals(type.getParent())) {
            throw new NotAllowedActionException("User already has this subscription");
        } else if (subscription.getType().getLevel() > type.getLevel()) {
            throw new NotAllowedActionException("User can't downgrade subscription");
        }
    }

    public static void validateUserSearch(UserData userData) {
        if (!userData.hasActiveSubscription()) {
            throw new SubscriptionPlanException("User has no active subscription, please renew or contact customer service");
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
