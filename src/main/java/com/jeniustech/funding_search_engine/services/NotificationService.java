package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.enums.AdminLogType;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.repository.SavedSearchRepository;
import com.jeniustech.funding_search_engine.services.solr.CallSolrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final CallSolrClientService callSolrClientService;
    private final SavedSearchRepository savedSearchRepository;
    private final AdminLogService adminLogService;

    public void sendAllNotifications() {
        List<SavedSearch> savedSearches = savedSearchRepository.findSavedSearchedForNotifications();
        for (SavedSearch savedSearch : savedSearches) {
            LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
            List<CallDTO> callDTOS = callSolrClientService.searchAfterDate(savedSearch.getValue(), startOfDay);
            if (!callDTOS.isEmpty()) {
                try {
                    emailService.sendNewCallsNotification(savedSearch, callDTOS);
                    adminLogService.addLog(AdminLogType.NOTIFICATION_SUCCESS, savedSearch.getUserData().getId() + "-" + savedSearch.getId());
                } catch (ScraperException e) {
                    adminLogService.addLog(AdminLogType.NOTIFICATION_FAIL, savedSearch.getUserData().getId() + "-" + savedSearch.getId());
                }
            }
        }

    }
}
