package com.jeniustech.funding_search_engine.scraper.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EUSearchResultDTO {

    int pageSize;
    int totalResults;
    List<EUCallDTO> results;

    @JsonIgnore
    public int getTotalPages() {
        return getTotalPages(pageSize);
    }

    @JsonIgnore
    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) totalResults / pageSize);
    }

}
