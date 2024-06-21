package com.jeniustech.funding_search_engine.config;

import com.jeniustech.funding_search_engine.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(HandlerMethodValidationException ex) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("")
                .build();
        ex.getAllValidationResults().forEach((error) -> {
            String path = error.getMethodParameter().getParameterName();
            String message = error.getResolvableErrors().iterator().next().getDefaultMessage();
            errorDTO.setMessage(errorDTO.getMessage().concat(path + " " + message + "\n"));

        });
        return ResponseEntity.badRequest().body(errorDTO);
    }
}
