package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.scraper.util.ScraperStringUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.jeniustech.funding_search_engine.mappers.DateMapper.csvFormatter;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class StringUtilTest {

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

}
