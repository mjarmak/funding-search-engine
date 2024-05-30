package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CallNotFoundException extends RuntimeException {

    public CallNotFoundException(String message) {
        super(message);
    }

}
