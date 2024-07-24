package com.jeniustech.funding_search_engine.util;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DetailFormatterTest {

//    @Test
    void formatTest() throws IOException {
        String expected = FileUtil.readFile("/test-data/formHtmlToTextOut.txt");
        String formatted = DetailFormatter.format(FileUtil.readFile("/test-data/formHtmlToText.txt"), DetailFormatter.FormatTypeEnum.TEXT);
        assertEquals(expected, formatted);
    }
}
