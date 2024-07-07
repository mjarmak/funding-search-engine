package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class EnumException extends RuntimeException {

    public EnumException() {
    }

    public EnumException(String message) {
        super(message);
    }

    public EnumException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnumException(Throwable cause) {
        super(cause);
    }

    public EnumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
