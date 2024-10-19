package com.jeniustech.funding_search_engine.loader;

import com.jeniustech.funding_search_engine.scraper.services.OrganisationDataLoader;
import com.jeniustech.funding_search_engine.scraper.services.ProjectDataLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DataLoderTest {

    @Autowired
    OrganisationDataLoader organisationDataLoader;

    @Autowired
    ProjectDataLoader projectDataLoader;

    @Test
    void validateOrganisationData() {
        List<String> files = List.of(
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organization_2021-2.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organization_2014-2.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/new/organizations-fp7-2007-2013-2.csv"
        );

        for (String file : files) {
            organisationDataLoader.splitFileAndLoadData(file, false, true, 1_000_000, true);
        }
    }

    @Test
    void validateProjectData() {
        List<String> files = List.of(
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/project_2021-2.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/project_2014-2.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/new/projects-fp7-2007-2013-2.csv"
        );

        for (String file : files) {
            projectDataLoader.splitFileAndLoadData(file, false, true, 1_000_000, true);
        }
    }

}
