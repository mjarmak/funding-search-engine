package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.enums.AdminLogType;
import com.jeniustech.funding_search_engine.scraper.services.*;
import com.jeniustech.funding_search_engine.services.AdminLogService;
import com.jeniustech.funding_search_engine.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final PartnerScraperService partnerScraperService;

    private final CallDataLoader callDataLoader;
    private final ProjectDataLoader projectDataLoader;
    private final OrganisationDataLoader organisationDataLoader;

    private final NotificationService notificationService;
    private final AdminLogService adminLogService;

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/calls/scrape")
    public void scrapeCalls(
            @RequestParam(value = "query", required = false) List<String> queries,
            @RequestParam(value = "files", required = false) List<String> files,
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
    public void scrapePartners(
            @RequestParam(value = "files") List<String> files,
            @RequestParam(required = false, defaultValue = "false") boolean oldFormat,
            @RequestParam(required = false, defaultValue = "false") boolean skipUpdate,
            @RequestParam(required = false, defaultValue = "10000") int rowsPerFile
            ) {
        log.info("Scraping partners");
        log.info("Skipping update: {}", skipUpdate);
        for (String file : files) {
            organisationDataLoader.splitFileAndLoadData(file, oldFormat, skipUpdate, rowsPerFile);
            adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, file);
        }
        log.info("Updating funding information");
        partnerScraperService.updateFundingInformation();
        adminLogService.addLog(AdminLogType.PARTNER_FUNDING_UPDATE, "Funding information updated");
        log.info("Funding information updated");
    }

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/partners/scrape/updateFundingInformation")
    public void scrapePartners() {
        log.info("Updating funding information");
        partnerScraperService.updateFundingInformation();
        adminLogService.addLog(AdminLogType.PARTNER_FUNDING_UPDATE, "Funding information updated");
        log.info("Funding information updated");
    }


    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/projects/scrape")
    public void scrapeProjects(
            @RequestParam(value = "files") List<String> files,
            @RequestParam(value = "oldFormat", required = false, defaultValue = "false") boolean oldFormat,
            @RequestParam(value = "skipUpdate", required = false, defaultValue = "false") boolean skipUpdate,
            @RequestParam(required = false, defaultValue = "10000") int rowsPerFile
            ) {
        log.info("Scraping projects");
        log.info("Skipping update: {}", skipUpdate);
        for (String file : files) {
            projectDataLoader.splitFileAndLoadData(file, oldFormat, skipUpdate, rowsPerFile);
            adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, file);
        }
    }
    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/projects/solr")
    public void loadProjectsSolr() {
        projectDataLoader.loadSolrData();
    }

    public void scrapeAndNotify(List<String> queries, List<String> files, String destination) {
        if (files == null) {
            files = new ArrayList<>();
        }
        if (files.isEmpty()) {
            for (String query : queries) {
                log.info("Scraping query: {}", query);
                final String file = scrapeService.scrapeCalls(query, destination);
                files.add(file);
            }
        }
        for (String file : files) {
            log.info("Loading file: {}", file);
            callDataLoader.loadData(file);
        }
        callDataLoader.loadSolrData();
        notificationService.sendAllNotifications();
        adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, String.join(",", queries));
    }

}
