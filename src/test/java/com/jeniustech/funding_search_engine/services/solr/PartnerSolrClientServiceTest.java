package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.PartnerDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import com.jeniustech.funding_search_engine.services.PartnerService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
public class PartnerSolrClientServiceTest extends SolrClientTest{

    @Autowired
    private PartnerSolrClientService service;

    @Autowired
    private PartnerService partnerService;

    @ParameterizedTest
    @CsvSource({
            "digital solutions, 1000",
            "digital, 200",
            "building, 70",
    })
    public void testSearch_count(String query, int count) {
        SearchDTO<PartnerDTO> results = service.search(query, 0, 5, JWT_MODEL, null);
        System.out.println("count: " + results.getTechnicalTotalResults());
        assertTrue(results.getTechnicalTotalResults() > count);
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({
            "digital solutions, 1000",
            "digital, 200",
            "building, 70",
    })
    public void testSearchByTopic_count(String query, int count) {
        SearchDTO<PartnerDTO> results = partnerService.searchByTopic(JWT_MODEL.getUserId(), query, null);
        System.out.println("count: " + results.getTechnicalTotalResults());
        assertTrue(results.getTechnicalTotalResults() > count);
    }
}
