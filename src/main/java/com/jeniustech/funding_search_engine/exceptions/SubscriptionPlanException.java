package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class SubscriptionPlanException extends RuntimeException {

    public SubscriptionPlanException() {
        super();
    }

    public SubscriptionPlanException(String message) {
        super(message);
    }

}
