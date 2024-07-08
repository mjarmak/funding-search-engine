package com.jeniustech.funding_search_engine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ReportException extends RuntimeException {

    public ReportException() {
        super();
    }

    public ReportException(String message) {
        super(message);
    }

}
