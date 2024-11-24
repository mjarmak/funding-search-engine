package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.dto.search.ProjectDTO;
import com.jeniustech.funding_search_engine.dto.search.SearchDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
public class ProjectSolrClientServiceTest extends SolrClientTest {

    @Autowired
    private ProjectSolrClientService service;

    @ParameterizedTest
    @CsvSource({
            "digital solutions, 1000",
            "digital, 2000",
            "building, 2000",
    })
    public void testSearch_count(String query, int count) {
        SearchDTO<ProjectDTO> results = service.search(query, 0, 5, null, null, JWT_MODEL);
        System.out.println("count: " + results.getTechnicalTotalResults());
        assertTrue(results.getTechnicalTotalResults() > count);
    }
}
