package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DocumentSaveException extends RuntimeException {

    public DocumentSaveException() {
    }

    public DocumentSaveException(String message) {
        super(message);
    }

    public DocumentSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentSaveException(Throwable cause) {
        super(cause);
    }

    public DocumentSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
