package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.SavedSearchDTO;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.NotAllowedActionException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.SavedSearchMapper;
import com.jeniustech.funding_search_engine.repository.SavedSearchRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedSearchService {

    private final SavedSearchRepository savedSearchRepository;
    private final UserDataRepository userDataRepository;

    public SavedSearchDTO saveSearch(String subjectId, SavedSearchDTO savedSearchDTO) {

        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<SavedSearch> existingSavedSearch = savedSearchRepository.findByUserIdAndValue(userData.getId(), savedSearchDTO.getValue());

        if (!existingSavedSearch.isEmpty()) {
            throw new NotAllowedActionException("Search already exists with the same value (" + savedSearchDTO.getName() + ")");
        }

        if (savedSearchDTO.getName().isEmpty()) {
            savedSearchDTO.setName("Search: " + savedSearchDTO.getValue());
        }

        SavedSearch savedSearch = SavedSearchMapper.map(savedSearchDTO);
        savedSearch.setUserData(userData);

        return SavedSearchMapper.map(savedSearchRepository.save(savedSearch));
    }

    public List<SavedSearchDTO> getUserSavedSearches(String subjectId) {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return SavedSearchMapper.map(savedSearchRepository.findByUserId(userData.getId()));
    }

    public SavedSearchDTO updateSearch(Long id, String userId, SavedSearchDTO savedSearchDTO) {
        UserData userData = userDataRepository.findBySubjectId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<SavedSearch> existingSavedSearch = savedSearchRepository.findByIdAndUserDataId(id, userData.getId());

        if (existingSavedSearch.isEmpty()) {
            throw new NotAllowedActionException("Search not found");
        }

        SavedSearch savedSearch = existingSavedSearch.get();

        if (!savedSearchDTO.getName().isEmpty()) {
            savedSearch.setName(savedSearchDTO.getName());
        }
        if (!savedSearchDTO.getValue().isEmpty()) {
            savedSearch.setValue(savedSearchDTO.getValue());
        }
        savedSearch.setNotification(savedSearchDTO.isNotification());

        return SavedSearchMapper.map(savedSearchRepository.save(savedSearch));
    }

    public void deleteSearch(Long id, String userId) {
        UserData userData = userDataRepository.findBySubjectId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<SavedSearch> existingSavedSearch = savedSearchRepository.findByIdAndUserDataId(id, userData.getId());

        if (existingSavedSearch.isEmpty()) {
            throw new NotAllowedActionException("Search not found");
        }

        savedSearchRepository.delete(existingSavedSearch.get());
    }
}
