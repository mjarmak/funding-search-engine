package com.jeniustech.funding_search_engine.loader;

import com.jeniustech.funding_search_engine.scraper.services.CallScrapeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CallScrapeServiceTest {

    @Autowired
    private CallScrapeService scrapeService;

    @Test
    void scrapeCallsOpen() {
        scrapeService.scrapeCalls("query-open", "C:/Projects/funding-search-engine/src/main/resources/scraper/out");
    }

    @Test
    void scrapeCallsUpcoming() {
        scrapeService.scrapeCalls("query-upcoming", "C:/Projects/funding-search-engine/src/main/resources/scraper/out");
    }

    @Test
    void scrapeCallsClosed() {
        scrapeService.scrapeCalls("query-closed", "C:/Projects/funding-search-engine/src/main/resources/scraper/out");
    }

}
