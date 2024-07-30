package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class IDataService<DTO> {
    public final UserDataRepository userDataRepository;
    abstract DTO getDTOById(Long id);
    abstract boolean isFavorite(Long id, Long userId);
    abstract void favorite(Long id, String subjectId);
    abstract void unFavorite(Long id, String subjectId);
    abstract SearchDTO<DTO> getFavoritesByUserId(String subjectId, int pageNumber, int pageSize);
    abstract List<Long> checkFavorites(UserData userData, List<Long> ids);


    public UserData getUserOrNotFound(String subjectId) {
        return userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
