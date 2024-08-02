package com.jeniustech.funding_search_engine.scraper.services;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.jeniustech.funding_search_engine.exceptions.SearchException;
import com.jeniustech.funding_search_engine.scraper.mappers.CSVMapper;
import com.jeniustech.funding_search_engine.scraper.models.CallCSVDetails;
import com.jeniustech.funding_search_engine.scraper.models.EUCallDTO;
import com.jeniustech.funding_search_engine.scraper.models.EUCallDetailDTO;
import com.jeniustech.funding_search_engine.scraper.models.EUSearchResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Paths;

@Slf4j
@Service
public class CallScrapeService {

    private final RestTemplate restTemplateSearch;
    private final RestTemplate restTemplateDetails;
    private final ResourceLoader resourceLoader;

    private final String searchAPIUrl = "https://api.tech.ec.europa.eu/search-api/prod/rest/search";

    private final String apiKey = "SEDIA";

    private final int pageSize = 100;

    public CallScrapeService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.restTemplateSearch = new RestTemplate();
        this.restTemplateDetails = new RestTemplate();
    }

    public String scrapeCalls(String query, String dest) {

        FileSystemResource queryFile;
        try {
            queryFile = getFile("query/" + query + ".json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileSystemResource languageFile;
        try {
            languageFile = getFile("query/languages.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EUSearchResultDTO infoQuery = getSearch(1, 1, queryFile, languageFile);

        if (infoQuery == null) {
            throw new SearchException("Failed to get search info");
        }

        if (infoQuery.getTotalResults() == 0) {
            throw new SearchException("No results found");
        }

        log.info("Total results: {}", infoQuery.getTotalResults());

        int totalPages = infoQuery.getTotalPages(pageSize);

        log.info("Total pages: {}", totalPages);


        String file = dest + "/output_" + query + "_" + System.currentTimeMillis() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write(String.join(",", CallCSVDetails.getCSVHeaders()));
            writer.newLine();

            for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) {
                log.info("Page: {}", pageNumber);
                EUSearchResultDTO searchResultDTO = getSearch(pageNumber, pageSize, queryFile, languageFile);

                if (searchResultDTO == null || searchResultDTO.getResults() == null) {
                    throw new SearchException("Failed to get data");
                }
                for (EUCallDTO item : searchResultDTO.getResults()) {
                    CallCSVDetails details;
                    if (item.hasJsonUrl()) {
                        try {
                            EUCallDetailDTO callDetails = getDetails(item.getJsonUrl());
                            details = CSVMapper.map(item, callDetails);
                        } catch (Exception e) {
                            log.error("Failed to map details for: {}", item.getJsonUrl());
                            details = CSVMapper.map(item);
                        }
                    } else {
                        details = CSVMapper.map(item);
                    }
                    if (details != null) {
                        log.debug(details.getIdentifier());
                        writer.write(String.join(",", details.getCSVValues()));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new SearchException("Failed to write to file", e);
        }



        log.info("Done");
        return file;
    }

    private FileSystemResource getFile(String filePath) throws IOException {
        FileSystemResource queryFile;
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        queryFile = new FileSystemResource(Paths.get(resource.getURI()));
        return queryFile;
    }

    private EUSearchResultDTO getSearch(int pageNumber, int pageSize, FileSystemResource queryFile, FileSystemResource languageFile) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("query", queryFile);
        body.add("languages", languageFile);

        try {
            return getSearch(pageNumber, pageSize, body);
        } catch (Exception e) {
            log.error("Failed to get search data. Retrying...");
            return getSearch(pageNumber, pageSize, queryFile, languageFile);
        }
    }

    private EUSearchResultDTO getSearch(int pageNumber, int pageSize, MultiValueMap<String, Object> body) throws ConnectException {
        return restTemplateSearch.postForObject(
                searchAPIUrl + "?pageNumber=" + pageNumber + "&pageSize=" + pageSize + "&apiKey=" + apiKey + "&text=***",
                body,
                EUSearchResultDTO.class
        );
    }

    private EUCallDetailDTO getDetails(String url) throws MismatchedInputException {
        try {
            log.debug("Getting details from: {}", url);
            return restTemplateDetails.getForObject(url, EUCallDetailDTO.class);
        } catch (HttpClientErrorException e) {
            log.error("Failed to get details from: {}", url);
            throw new SearchException("Failed to get details from: " + url);
        }
    }

    private EUCallDTO getDetailsFromSearchAPI(String url) {
        return restTemplateDetails.getForObject(url, EUCallDTO.class);
    }

}
