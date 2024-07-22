package com.jeniustech.funding_search_engine.repository;

import com.jeniustech.funding_search_engine.entities.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {

        @Query("SELECT s FROM SavedSearch s WHERE s.userData.id = :userId")
        List<SavedSearch> findByUserId(Long userId);

        Optional<SavedSearch> findByIdAndUserDataId(Long id, Long userId);

        @Query("SELECT s FROM SavedSearch s WHERE s.userData.id = :userId AND s.value = :value")
        List<SavedSearch> findByUserIdAndValue(Long userId, String value);

        @Query("SELECT s FROM SavedSearch s WHERE s.notification = 1")
        List<SavedSearch> findSavedSearchedForNotifications();
}
