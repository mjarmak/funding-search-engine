package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TrialSubscriptionException extends RuntimeException {

    public TrialSubscriptionException() {
        super();
    }

    public TrialSubscriptionException(String message) {
        super(message);
    }

}
