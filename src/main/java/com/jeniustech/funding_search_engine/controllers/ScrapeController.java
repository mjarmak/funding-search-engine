package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.enums.AdminLogType;
import com.jeniustech.funding_search_engine.scraper.services.CallDataLoader;
import com.jeniustech.funding_search_engine.scraper.services.CallScrapeService;
import com.jeniustech.funding_search_engine.scraper.services.PartnerScraperService;
import com.jeniustech.funding_search_engine.services.AdminLogService;
import com.jeniustech.funding_search_engine.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScrapeController {

    private final CallScrapeService scrapeService;
    private final PartnerScraperService partnerScraperService;
    private final CallDataLoader callDataLoader;
    private final NotificationService notificationService;
    private final AdminLogService adminLogService;

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/calls/scrape")
    public void scrapeCalls(
            @RequestParam(value = "query", required = false) List<String> queries,
            @RequestParam(value = "files") List<String> files,
            @RequestParam(value = "destination")
            String destination
    ) {
        scrapeAndNotify(queries, files, destination);
    }
    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/calls/solr")
    public void loadCallSolr() {
        callDataLoader.loadSolrData();
    }

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/partners/scrape")
    public void scrapePartners() {
        scraperPartners();
    }

    public void scrapeAndNotify(List<String> queries, List<String> files, String destination) {
        if (files.isEmpty()) {
            for (String query : queries) {
                log.info("Scraping query: {}", query);
                final String file = scrapeService.scrapeCalls(query, destination);
                files.add(file);
            }
        }
        for (String file : files) {
            log.info("Loading file: {}", file);
            callDataLoader.loadEntities(file);
            callDataLoader.loadSolrData();
        }
        notificationService.sendAllNotifications();
        adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, String.join(",", queries));
    }

    public void scraperPartners() {
        log.info("Updating funding information");
        partnerScraperService.updateFundingInformation();
        adminLogService.addLog(AdminLogType.PARTNER_FUNDING_UPDATE, "Funding information updated");
    }

}
