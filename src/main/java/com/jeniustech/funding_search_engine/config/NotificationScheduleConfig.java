package com.jeniustech.funding_search_engine.config;

import com.jeniustech.funding_search_engine.controllers.ScrapeController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduleConfig {

    @Value("${scraper.directory}")
    private String directory;

    private final ScrapeController scrapeController;

    @Scheduled(cron = "0 0 5 * * ?")
//    @Scheduled(fixedRate = 10000) // test
    @Transactional
    public void performTask() {
        System.out.println("Task executed at 5 AM");
        scrapeController.scrapeAndNotify(
                List.of("query-upcoming"),
                new ArrayList<>(), directory, true
        );
    }
}
