package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.scraper.services.CallDataLoader;
import com.jeniustech.funding_search_engine.scraper.services.CallScrapeService;
import com.jeniustech.funding_search_engine.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScrapeController {

    private final CallScrapeService scrapeService;
    private final CallDataLoader callDataLoader;
    private final NotificationService notificationService;

    @GetMapping("/scrape")
    public void scrape(
            @RequestParam(value = "query") List<String> queries,
            @RequestParam(value = "destination")
            String destination
    ) {
        final List<String> files = new ArrayList<>();
        for (String query : queries) {
            log.info("Scraping query: {}", query);
            final String file = scrapeService.scrapeCalls(query, destination);
            files.add(file);
        }
        for (String file : files) {
            log.info("Loading file: {}", file);
            callDataLoader.loadFile(file);
        }
        notificationService.sendAllNotifications();
    }

}
