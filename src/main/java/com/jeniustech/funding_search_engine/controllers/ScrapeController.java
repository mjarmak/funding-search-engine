package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.enums.AdminLogType;
import com.jeniustech.funding_search_engine.enums.FrameworkProgramEnum;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.scraper.services.*;
import com.jeniustech.funding_search_engine.services.AdminLogService;
import com.jeniustech.funding_search_engine.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
            @RequestParam(required = false, defaultValue = "false") boolean skipUpdate,
            @RequestParam(required = false, defaultValue = "false") boolean secret,
            @RequestParam(required = false) String destination
    ) {
        scrapeAndNotify(queries, files, destination, skipUpdate, secret);
    }

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/calls/solr")
    public void loadCallSolr() {
        callDataLoader.loadSolrData();
    }

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/calls/updateProjectNumbers")
    public void updateProjectNumbers() {
        callDataLoader.updateProjectNumbers();
    }


    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/partners/solr")
    public void loadPartnerSolr() {
        organisationDataLoader.loadSolrData();
    }

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/partners/scrape")
    public void scrapePartners(
            @RequestParam(value = "files") List<String> files,
            @RequestParam(required = false, defaultValue = "false") boolean oldFormat,
            @RequestParam(required = false, defaultValue = "false") boolean skipUpdate,
            @RequestParam(required = false, defaultValue = "false") boolean onlyValidate,
            @RequestParam(required = false, defaultValue = "10000") int rowsPerFile
            ) {
        log.info("Scraping partners");
        log.info("Skipping update: {}", skipUpdate);
        log.info("Old format: {}", oldFormat);
        log.info("Only validate: {}", onlyValidate);
        for (String file : files) {
            organisationDataLoader.splitFileAndLoadData(file, oldFormat, skipUpdate, rowsPerFile, onlyValidate);
            adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, file);
        }
        log.info("Updating funding information");
        if (!onlyValidate) {
            partnerScraperService.updateFundingInformation();
            adminLogService.addLog(AdminLogType.PARTNER_FUNDING_UPDATE, "Funding information updated");
            log.info("Funding information updated");
        }
    }

    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/partners/scrape/updateFundingInformation")
    public void updatePartnerFundingInformation() {
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
            @RequestParam(required = false, defaultValue = "false") boolean onlyValidate,
            @RequestParam(required = false, defaultValue = "false") boolean forceSaveProblems,
            @RequestParam(required = false, defaultValue = "10000") int rowsPerFile
            ) {
        log.info("Scraping projects");
        log.info("Skipping update: {}", skipUpdate);
        log.info("Old format: {}", oldFormat);
        log.info("Only validate: {}", onlyValidate);
        for (String file : files) {
            projectDataLoader.splitFileAndLoadData(file, oldFormat, skipUpdate, rowsPerFile, onlyValidate, forceSaveProblems);
            adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, file);
        }
    }
    @PreAuthorize("hasRole('admin-server')")
    @GetMapping("/projects/solr")
    public void loadProjectsSolr(
            @RequestParam(required = false, name = "program", defaultValue =
                    "HORIZON," +
                            "H2020," +
                            "FP7"
            ) List<FrameworkProgramEnum> frameworkPrograms
            ) {
        projectDataLoader.loadSolrData(frameworkPrograms);
    }

    public void scrapeAndNotify(
            List<String> queries,
            List<String> files,
            String destination,
            boolean skipUpdate,
            boolean secret) {
        if (files == null) {
            files = new ArrayList<>();
        }
        if (files.isEmpty()) {
            if (destination == null) {
                throw new ScraperException("Destination is required");
            }
            for (String query : queries) {
                log.info("Scraping query: {}", query);
                final String file = scrapeService.scrapeCalls(query, destination);
                files.add(file);
            }
        }
        for (String file : files) {
            log.info("Loading file: {}", file);
            callDataLoader.loadData(file, skipUpdate, secret);
        }
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        callDataLoader.loadSolrData(startOfDay);
        notificationService.sendAllNotifications();
        if (queries != null && !queries.isEmpty()) {
            adminLogService.addLog(AdminLogType.SCRAPE_SUCCESS, String.join(",", queries));
        }
    }

}
