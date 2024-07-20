package com.jeniustech.funding_search_engine.util;

import com.jeniustech.funding_search_engine.mappers.DateMapper;
import org.junit.jupiter.api.Test;

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

//    @Test
//    void removeHtmlTags() {
//        assertEquals("Hello World", StringUtil.removeHtmlTags("Hello <b>World</b>"));
//        assertEquals("Competence Hub Open Call - EIT Urban Mobility", StringUtil.removeHtmlTags("<p><a href=\"\"https://www.eiturbanmobility.eu/competence-hub-open-call/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Competence Hub Open Call - EIT Urban Mobility</a></p>"));
//        assertEquals("greenSME manufacturing SME pathway description:  Manufacturing SMEs â€“ greenSME (greensmehub.eu)Sustainability and technology providers: Sustainability & Technology Providers â€“ greenSME (greensmehub.eu)", StringUtil.removeHtmlTags("<p><br></p><p>greenSME <strong>manufacturing SME pathway</strong> description:  <a href=\"\"https://greensmehub.eu/manufacturing-smes/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Manufacturing SMEs â€“ greenSME (greensmehub.eu)</a></p><p><br></p><p><strong>Sustainability and technology providers</strong>: <a href=\"\"https://greensmehub.eu/sustainability-technology-providers/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Sustainability & Technology Providers â€“ greenSME (greensmehub.eu)</a></p><p><br></p>"));
//    }
//
//    @Test
//    void processString() {
//        assertEquals("Hello World", StringUtil.processString("Hello <b>World</b>", false));
//        assertEquals("Competence Hub Open Call - EIT Urban Mobility", StringUtil.processString("<p><a href=\"\"https://www.eiturbanmobility.eu/competence-hub-open-call/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Competence Hub Open Call - EIT Urban Mobility</a></p>", false));
//        assertEquals("greenSME manufacturing SME pathway description: Manufacturing SMEs - greenSME (greensmehub.eu)Sustainability and technology providers: Sustainability & Technology Providers - greenSME (greensmehub.eu)",
//                StringUtil.processString("<p><br></p><p>greenSME <strong>manufacturing SME pathway</strong> description:  <a href=\"\"https://greensmehub.eu/manufacturing-smes/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Manufacturing SMEs â€“ greenSME (greensmehub.eu)</a></p><p><br></p><p><strong>Sustainability and technology providers</strong>: <a href=\"\"https://greensmehub.eu/sustainability-technology-providers/\"\" rel=\"\"noopener noreferrer\"\" target=\"\"_blank\"\">Sustainability & Technology Providers â€“ greenSME (greensmehub.eu)</a></p><p><br></p>", false));
//    }

    @Test
    void removeStrangeCharacters() {
        assertEquals("This is a test ' example's.", StringUtil.processString("This is a test â€™ exampleâ€™s.", false));
    }

    @Test
    void removeMultiSpaces() {
        assertEquals("t t", StringUtil.processString("t     t", false));
        assertEquals(null, StringUtil.processString("     ", false));
        assertEquals(
                "Types of activities (art 10(3) EDF Regulation) Eligible? (a) Activity",
                StringUtil.processString("Types of activities (art 10(3) EDF Regulation)      Eligible?                (a)      Activity", false));
    }

}
