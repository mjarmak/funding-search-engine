package com.jeniustech.funding_search_engine.loader;

import com.jeniustech.funding_search_engine.scraper.services.OrganisationDataLoader;
import com.jeniustech.funding_search_engine.scraper.services.ProjectDataLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DataLoaderTest {

    @Autowired
    OrganisationDataLoader organisationDataLoader;

    @Autowired
    ProjectDataLoader projectDataLoader;

    @Test
    void validateOrganisationData() {
        List<String> files = List.of(
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organization_2021-2.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organization_2014-2.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp7-2007-2013.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp7-2007-2013-2.csv"
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
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp7-2007-2013.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp7-2007-2013-2.csv"
        );

        for (String file : files) {
            projectDataLoader.splitFileAndLoadData(file, false, true, 1_000_000, true);
        }
    }

    @Test
    void validateOrganisationDataOldFormat() {
        List<String> files = List.of(
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp1-1984–1987.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp2-1987-1991.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp3-1990-1994.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp4-1994-1998.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp5-1998-2002.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/organizations-fp6-2002-2006.csv"
        );

        for (String file : files) {
            organisationDataLoader.splitFileAndLoadData(file, true, true, 1_000_000, true);
        }
    }

    @Test
    void validateProjectDataOldFormat() {
        List<String> files = List.of(
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp1-1984–1987.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp2-1987-1991.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp3-1990-1994.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp4-1994-1998.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp5-1998-2002.csv",
                "C:/Projects/funding-search-engine/src/test/resources/data/projects/projects-fp6-2002-2006.csv"
        );

        for (String file : files) {
            projectDataLoader.splitFileAndLoadData(file, true, true, 1_000_000, true);
        }
    }

}
