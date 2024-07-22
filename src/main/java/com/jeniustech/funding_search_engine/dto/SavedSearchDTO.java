package com.jeniustech.funding_search_engine.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SavedSearchDTO {
    private Long id;
    private String name;
    private String value;
    private boolean notification;
}
