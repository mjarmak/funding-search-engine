package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class NotAllowedActionException extends RuntimeException {

    public NotAllowedActionException() {
        super();
    }

    public NotAllowedActionException(String message) {
        super(message);
    }

}
