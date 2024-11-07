package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailServiceException extends Exception {

    public EmailServiceException(String message) {
        super(message);
    }

}
