package com.jeniustech.funding_search_engine.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorDTO {
    private int status;
    private String message;
}
