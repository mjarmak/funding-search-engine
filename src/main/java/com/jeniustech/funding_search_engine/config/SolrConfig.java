package com.jeniustech.funding_search_engine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(
        basePackages = "com.jeniustech.funding_search_engine.repository.solr")
@ComponentScan
@RequiredArgsConstructor
public class SolrConfig {

    @Value("${spring.data.solr.host}")
    private String url;

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(url).build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) throws Exception {
        return new SolrTemplate(client);
    }
}
