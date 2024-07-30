package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.LogBook;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.enums.LogTypeEnum;
import com.jeniustech.funding_search_engine.repository.LogBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogBookRepository logBookRepository;

    public void addLog(UserData userData, LogTypeEnum logTypeEnum, String text) {
        if (logTypeEnum == LogTypeEnum.SEARCH_CALL || logTypeEnum == LogTypeEnum.SEARCH_PROJECT || logTypeEnum == LogTypeEnum.SEARCH_PARTNER) {
            LogBook lastLog = getLastLogByUserIdAndType(userData.getId(), logTypeEnum);
            if (lastLog != null && lastLog.getLogText().equals(text)) {
                return;
            }
        }
        logBookRepository.save(LogBook.builder()
                .userData(userData)
                .type(logTypeEnum)
                .logText(text)
                .build());
    }

    public Long getCountByUserIdAndType(Long id, LogTypeEnum logTypeEnum) {
        return logBookRepository.countByUserDataIdAndType(id, logTypeEnum);
    }

    public List<LogBook> getLogsByUserIdAndType(Long id, LogTypeEnum logTypeEnum, int limit) {
        return logBookRepository.findByUserDataIdAndType(id, logTypeEnum, limit);
    }

    private LogBook getLastLogByUserIdAndType(Long id, LogTypeEnum logTypeEnum) {
        return logBookRepository.findLastByUserDataIdAndType(id, logTypeEnum);
    }

}
