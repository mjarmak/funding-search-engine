package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.SavedSearchDTO;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.enums.BooleanEnum;

import java.util.List;
import java.util.stream.Collectors;

public interface SavedSearchMapper {

    static SavedSearchDTO map(SavedSearch savedSearch) {
        return SavedSearchDTO.builder()
                .id(savedSearch.getId())
                .name(savedSearch.getName())
                .value(savedSearch.getValue())
                .notification(savedSearch.getNotification().toBoolean())
                .build();
    }

    static List<SavedSearchDTO> map(List<SavedSearch> savedSearches) {
        return savedSearches.stream()
                .map(SavedSearchMapper::map)
                .collect(Collectors.toList());
    }

    static SavedSearch map(SavedSearchDTO savedSearchDTO) {
        return SavedSearch.builder()
                .id(savedSearchDTO.getId())
                .name(savedSearchDTO.getName().substring(0, Math.min(savedSearchDTO.getName().length(), 255)))
                .value(savedSearchDTO.getValue().substring(0, Math.min(savedSearchDTO.getValue().length(), 255)))
                .notification(BooleanEnum.fromBoolean(savedSearchDTO.isNotification()))
                .build();
    }

}
