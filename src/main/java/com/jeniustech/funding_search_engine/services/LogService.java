package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.LogBook;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.repository.LogBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogBookRepository logBookRepository;

    public void addLog(UserData userData, LogTypeEnum logTypeEnum, String text) {
        logBookRepository.save(LogBook.builder()
                .userData(userData)
                .type(logTypeEnum)
                .logText(text)
                .build());
    }

}
