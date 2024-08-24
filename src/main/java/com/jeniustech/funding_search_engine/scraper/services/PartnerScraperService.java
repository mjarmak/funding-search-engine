package com.jeniustech.funding_search_engine.scraper.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerScraperService {

    private final JdbcTemplate jdbcTemplate;

    public void updateFundingInformation() {
        String sql = """
            WITH funding_summary AS (
                SELECT
                    organisation_id,
                    SUM(funding_organisation) AS total_funding_organisation,
                    SUM(funding_eu) AS total_funding_eu,
                    COUNT(*) AS total_project_number
                FROM
                    organisation_project_join
                GROUP BY
                    organisation_id
            )
            UPDATE
                organisations o
            SET
                funding_organisation = fs.total_funding_organisation,
                funding_eu = fs.total_funding_eu,
                project_number = fs.total_project_number
            FROM
                funding_summary fs
            WHERE
                o.id = fs.organisation_id
        """;

        jdbcTemplate.execute(sql);
    }
}
