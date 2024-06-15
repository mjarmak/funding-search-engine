package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundItemException extends RuntimeException {

    public NotFoundItemException() {
        super();
    }

    public NotFoundItemException(String message) {
        super(message);
    }

}
