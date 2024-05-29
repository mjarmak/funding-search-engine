package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class SearchDTO <T> {
    List<T> results;
    Long totalResults;
}
