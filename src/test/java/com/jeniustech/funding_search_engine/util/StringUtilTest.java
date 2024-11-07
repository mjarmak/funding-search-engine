package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.jeniustech.funding_search_engine.mappers.DateMapper.csvFormatter;
import static org.junit.jupiter.api.Assertions.*;


public class StringUtilTest {

    @Test
    void removeSpecialCharacters_nullInput() {
        assertNull(StringUtil.removeSpecialCharacters(null));
    }

    @Test
    void removeSpecialCharacters_emptyString() {
        assertEquals("", StringUtil.removeSpecialCharacters(""));
        assertEquals("", StringUtil.removeSpecialCharacters(" "));
    }

    @Test
    void removeSpecialCharacters_noSpecialCharacters() {
        assertEquals("test query", StringUtil.removeSpecialCharacters("test query"));
    }

    @Test
    void removeSpecialCharacters_withSpecialCharacters() {
        assertEquals("test query", StringUtil.removeSpecialCharacters("test@# query!$"));
        assertEquals("HORIZON-CL5-2024-D4-02-05", StringUtil.removeSpecialCharacters("HORIZON-CL5-2024-D4-02-05"));
        assertEquals("HORIZON_CL5_2024_D4_02_05", StringUtil.removeSpecialCharacters("HORIZON_CL5_2024_D4_02_05"));
    }

    @Test
    void removeSpecialCharacters_onlySpecialCharacters() {
        assertEquals("", StringUtil.removeSpecialCharacters("@#$%^&*()"));
    }

    @Test
    void removeSpecialCharacters_mixedCharacters() {
        assertEquals("test query 123", StringUtil.removeSpecialCharacters("test@# query!$ 123"));
    }

    @Test
    void removeQuotes_nullInput() {
        assertEquals("", StringUtil.removeQuotes(null));
    }

    @Test
    void removeQuotes_noQuotes() {
        assertEquals("test query", StringUtil.removeQuotes("test query"));
    }

    @Test
    void removeQuotes_withQuotes() {
        assertEquals("test query", StringUtil.removeQuotes("\"test query\""));
    }

    @Test
    void removeQuotes_multipleQuotes() {
        assertEquals("test query", StringUtil.removeQuotes("\"test\" query\""));
    }

    @Test
    void removeQuotes_multipleQuotes_2() {
        assertEquals("\"test\" query\" test", StringUtil.removeQuotes("\"test\" query\" test"));
    }

    @Test
    void removeQuotes_onlyQuotes() {
        assertEquals("", StringUtil.removeQuotes("\"\""));
    }
    @Test
    void processQuery_nullInput() {
        assertEquals("", StringUtil.processQuery(null));
    }

    @Test
    void processQuery_emptyString() {
        assertEquals("", StringUtil.processQuery(""));
    }

    @Test
    void processQuery_noQuotesNoComma() {
        assertEquals("test query", StringUtil.processQuery("test query"));
    }

    @Test
    void processQuery_noQuotesWithComma() {
        assertEquals("test query", StringUtil.processQuery("test,query"));
    }

    @Test
    void processQuery_withQuotesNoComma() {
        assertEquals("test query", StringUtil.processQuery("\"test query\""));
    }

    @Test
    void processQuery_withQuotesWithComma() {
        assertEquals("test query", StringUtil.processQuery("\"test,query\""));
    }

    @Test
    void processQuery_multipleCommas() {
        assertEquals("test query example", StringUtil.processQuery("test,query,example"));
    }

    @Test
    void processQuery_multipleCommasWithQuotes() {
        assertEquals("test query example", StringUtil.processQuery("\"test,query,example\""));
    }

    @Test
    void isQuotedTwice_nullInput() {
        assertFalse(StringUtil.isQuotedTwice(null));
    }

    @Test
    void isQuotedTwice_emptyString() {
        assertFalse(StringUtil.isQuotedTwice(""));
    }

    @Test
    void isQuotedTwice_lessThanFourCharacters() {
        assertFalse(StringUtil.isQuotedTwice("abc"));
    }

    @Test
    void isQuotedTwice_notQuoted() {
        assertFalse(StringUtil.isQuotedTwice("test query"));
    }

    @Test
    void isQuotedTwice_singleQuotes() {
        assertFalse(StringUtil.isQuotedTwice("\"test query\""));
    }

    @Test
    void isQuotedTwice_doubleQuotes() {
        assertTrue(StringUtil.isQuotedTwice("\"\"test query\"\""));
    }

    @Test
    void isQuotedTwice_doubleQuotes_2() {
        assertTrue(StringUtil.isQuotedTwice("\"\"\"\""));
    }

    @Test
    void isQuotedTwice_mismatchedQuotes() {
        assertFalse(StringUtil.isQuotedTwice("\"\"test query\""));
    }

    @Test
    void toUTC() {
        assertEquals("2014-08-28T17:00:00", DateMapper.toUTC("2014-08-28T19:00:00.000+0200"));
        assertEquals("2014-08-28T18:00:00", DateMapper.toUTC("2014-08-28T19:00:00.000+0100"));
        assertEquals("2014-08-28T19:00:00", DateMapper.toUTC("2014-08-28T19:00:00.000+0000"));
    }

    @Test
    void getLocalDateTime() {
        assertEquals("2014-08-28T17:00:00", DateMapper.getLocalDateTime("2014-08-28T19:00:00.000+0200").format(csvFormatter));
        assertEquals("2014-08-28T18:00:00", DateMapper.getLocalDateTime("2014-08-28T19:00:00.000+0100").format(csvFormatter));
        assertEquals("2014-08-28T19:00:00", DateMapper.getLocalDateTime("2014-08-28T19:00:00.000+0000").format(csvFormatter));
    }

    @Test
    void processString_1_large() throws IOException {
        // read from file removeUselessHtml.txt
        String text = FileUtil.readFile("/test-data/removeUselessHtml.txt");
        String out = FileUtil.readFile("/test-data/removeUselessHtmlOut.txt");
        assertEquals(out, StringUtil.processString(text, false));
    }

    @Test
    void processString() {
        assertEquals("Hello World", ScraperStringUtil.removeHtmlTags("Hello <b>World</b>"));
        assertEquals("Competence Hub Open Call - EIT Urban Mobility", ScraperStringUtil.removeHtmlTags("<p><a href=\"\"https://www.eiturbanmobility.eu/competence-hub-open-call/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Competence Hub Open Call - EIT Urban Mobility</a></p>"));
        assertEquals(
                "greenSME manufacturing SME pathway description: Manufacturing SMEs - greenSME (greensmehub.eu)Sustainability and technology providers: Sustainability & Technology Providers - greenSME (greensmehub.eu)", ScraperStringUtil.removeHtmlTags(
                        "greenSME manufacturing SME pathway description: Manufacturing SMEs - greenSME (greensmehub.eu)Sustainability and technology providers: Sustainability & Technology Providers - greenSME (greensmehub.eu)"));
    }

    @Test
    void processString_2() {
        assertEquals("<p target=\"\"_blank\"\" href=\"\"test\"\">test</a>", StringUtil.processString("<p target=_self href=\"test\">test</a>", false));
        assertEquals("<p target=\"\"_blank\"\" href=\"\"test\"\">test</a>", StringUtil.processString("<p target=\"_self\" href=\"test\">test</a>", false));
        assertEquals("<p target=\"\"_blank\"\" href=\"\"test\"\">test</a>", StringUtil.processString("<p href=\"test\">test</a>", false));
        assertEquals("", StringUtil.processString("<a target=\"_self\" href=\"#r1\">[1]</a>", false));
        assertEquals("<a target=\"\"_blank\"\" href=\"\"http://euraxess.ec.europa.eu/sites/default/files/policy_library/principles_for_innovative_doctoral_training.pdf\"\">EU Principles on Innovative Doctoral Training</a>", StringUtil.processString("<a href=\"http://euraxess.ec.europa.eu/sites/default/files/policy_library/principles_for_innovative_doctoral_training.pdf\">EU Principles on Innovative Doctoral Training</a>", false));
        assertEquals("", StringUtil.processString("<a target=\"_self\" href=\"#fn1\">test</a>", false));
        assertEquals("", StringUtil.processString("<a   target=_self href=#r1>[1]</a>", false));
        assertEquals("", StringUtil.processString("<sup><a target=_self href=#r1>test</a></sup>", false));
        assertEquals("This is a test ' example's.", StringUtil.processString("This is a test ‘ example‘s.", false));
        assertEquals(
                "\"<p>This can be considered under the category of 'other goods, works and services'</p>\"", StringUtil.processString(
                "<p id=fn1>This can be considered under the category of 'other goods, works and services'</p>", false));
    }

    @Test
    void processString_3() {
        assertEquals("t t", StringUtil.processString("t     t", false));
        assertEquals("", StringUtil.processString("     ", false));
        assertEquals(
                "Types of activities (art 10(3) EDF Regulation) Eligible? (a) Activity",
                StringUtil.processString("Types of activities (art 10(3) EDF Regulation)      Eligible?                (a)      Activity", false));
    }

    @Test
    void isDifferentTest() {
        assertTrue(ScraperStringUtil.isDifferent("nope", "test", false));
        assertFalse(ScraperStringUtil.isDifferent("test", "test", false));
        assertFalse(ScraperStringUtil.isDifferent("", "test", false));
        assertFalse(ScraperStringUtil.isDifferent("test", "", false));
        assertFalse(ScraperStringUtil.isDifferent("", "", false));
        assertFalse(ScraperStringUtil.isDifferent(null, "", false));
        assertFalse(ScraperStringUtil.isDifferent("", "test", false));
        assertFalse(ScraperStringUtil.isDifferent(null, "test", false));
        assertFalse(ScraperStringUtil.isDifferent("test", null, false));

        assertFalse(ScraperStringUtil.isDifferent("test", "null", false));
        assertFalse(ScraperStringUtil.isDifferent("null", "test", false));

        assertFalse(ScraperStringUtil.isDifferent("null", "null", false));


        assertTrue(ScraperStringUtil.isDifferent("nope", "test", true));
        assertFalse(ScraperStringUtil.isDifferent("test", "test", true));
        assertTrue(ScraperStringUtil.isDifferent("", "test", true));
        assertTrue(ScraperStringUtil.isDifferent("test", "", true));
        assertTrue(ScraperStringUtil.isDifferent("", "", true));
        assertTrue(ScraperStringUtil.isDifferent(null, "", true));
        assertTrue(ScraperStringUtil.isDifferent("", "test", true));
        assertTrue(ScraperStringUtil.isDifferent(null, "test", true));
        assertTrue(ScraperStringUtil.isDifferent("test", null, true));

        assertTrue(ScraperStringUtil.isDifferent("test", "null", true));
        assertTrue(ScraperStringUtil.isDifferent("null", "test", true));

        assertTrue(ScraperStringUtil.isDifferent("null", "null", true));

    }
}
