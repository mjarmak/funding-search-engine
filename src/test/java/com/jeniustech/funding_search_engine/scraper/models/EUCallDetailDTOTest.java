package com.jeniustech.funding_search_engine.scraper.models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeniustech.funding_search_engine.scraper.mappers.CSVMapper;
import com.jeniustech.funding_search_engine.util.FileUtil;
import com.jeniustech.funding_search_engine.util.testUtil.Library;
import com.jeniustech.funding_search_engine.util.testUtil.Song;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EUCallDetailDTOTest {

    /**
     * Budget item is a linked hashmap/object
     */
    @Test
    void CSVMapperTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        EUCallDetailDTO callDetails = objectMapper.readValue(
                FileUtil.readFile("test-data/call_details_dto_10_11_24.json"),
                EUCallDetailDTO.class
        );

        EUCallDTO callDTO = objectMapper.readValue(
                FileUtil.readFile(
                "test-data/call_dto_10_06_24.json"),
                EUCallDTO.class
        );

        CallCSVDetails callCSVDetails = CSVMapper.map(callDTO, callDetails);
    }

    /**
     * Budget item is a list
     */
    @Test
    void CSVMapperTest_2() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        EUCallDetailDTO callDetails = objectMapper.readValue(
                FileUtil.readFile("test-data/call_details_dto_10_12_24.json"),
                EUCallDetailDTO.class
        );

        EUCallDTO callDTO = objectMapper.readValue(
                FileUtil.readFile(
                "test-data/call_dto_10_06_24.json"),
                EUCallDTO.class
        );

        CallCSVDetails callCSVDetails = CSVMapper.map(callDTO, callDetails);
    }

    /**
     * Budget item is a list, with many items
     */
    @Test
    void CSVMapperTest_3() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        EUCallDetailDTO callDetails = objectMapper.readValue(
                FileUtil.readFile("test-data/call_details_dto_10_12_24_2.json"),
                EUCallDetailDTO.class
        );

        EUCallDTO callDTO = objectMapper.readValue(
                FileUtil.readFile(
                "test-data/call_dto_10_06_24.json"),
                EUCallDTO.class
        );

        CallCSVDetails callCSVDetails = CSVMapper.map(callDTO, callDetails);
    }

    @Test
    void mapDetailsJsonToEUCallDetailDTOTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        String json = FileUtil.readFile("test-data/call_details_dto_10_06_24.json");
        EUCallDetailDTO callDetails = objectMapper.readValue(json, EUCallDetailDTO.class);

        assertEquals("HORIZON-WIDERA-2025-ACCESS-01-01-two-stage", callDetails.getTopicDetails().getIdentifier());
        assertEquals("Teaming for Excellence", callDetails.getTopicDetails().getTitle());
        assertEquals("test", callDetails.getTopicDetails().getDescription());
        assertEquals("test", callDetails.getTopicDetails().getConditions());

        assertEquals("Teaming for Excellence", callDetails.getTopicDetails().getCallTitle());

        assertEquals("8000000", callDetails.getMinBudget());
        assertEquals("15000000", callDetails.getMaxBudget());
        assertEquals(18, callDetails.getNumberOfGrants());
    }

    @Test
    void mapEUSearchResultDTOTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        String json = FileUtil.readFile("test-data/call_search_10_06_24.json");
        objectMapper.readValue(json, EUSearchResultDTO.class);
    }

    @Test
    void mapEUCallDTOTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        String json = FileUtil.readFile("test-data/call_dto_10_06_24.json");
        EUCallDTO callDTO = objectMapper.readValue(json, EUCallDTO.class);

        CallCSVDetails callCSVDetails = CSVMapper.map(callDTO);
    }

    @Test
    void mapBudgetOverviewTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        String json = FileUtil.readFile("test-data/call_dto_budget_overview.json");
        objectMapper.readValue(json, EUCallDTO.BudgetOverviewDTO.class);
    }

    @Test
    void objectMapperTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();


        String json = FileUtil.readFile("test-data/object_mapper_test.json");
        Library lib = objectMapper.readValue(json, Library.class);

        assertEquals("My Library", lib.getLibraryname());

        List<Song> songs = lib.getSongs();
        assertEquals(3, songs.size());

        assertEquals("Aaron", songs.get(0).getArtistName());
        assertEquals("Beautiful", songs.get(0).getSongName());

        assertEquals("Britney", songs.get(1).getArtistName());
        assertEquals("Oops I did It Again", songs.get(1).getSongName());

        assertEquals("Britney", songs.get(2).getArtistName());
        assertEquals("Stronger", songs.get(2).getSongName());

    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
