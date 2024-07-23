package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.AdminLogBook;
import com.jeniustech.funding_search_engine.enums.AdminLogType;
import com.jeniustech.funding_search_engine.repository.AdminLogBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogBookRepository adminLogBookRepository;

    public void addLog(AdminLogType logTypeEnum, String text) {
        adminLogBookRepository.save(AdminLogBook.builder()
                .type(logTypeEnum)
                .logText(text)
                .build());
    }

}
