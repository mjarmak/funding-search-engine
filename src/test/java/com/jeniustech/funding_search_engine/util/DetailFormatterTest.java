package com.jeniustech.funding_search_engine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DetailFormatterTest {

    @Test
    void formatTest() {
        assertEquals(
                "This is a test string with some numbers in brackets. ",
                DetailFormatter.format("This is a test[123] string with some[456] numbers in brackets.", DetailFormatter.FormatTypeEnum.HTML));
    }

    @Test
    void formatTest2() {

        assertEquals(
                "This is a test.1234: string: 5678; test; test test, test with: numbers: and no spaces. Here's a trick.3.4.5.",
                DetailFormatter.format("This is a test.1234: string:5678; test;test test,test with: numbers: and no spaces. Here's a trick.3.4.5.", DetailFormatter.FormatTypeEnum.HTML));

        assertEquals(
                "1.2.3.",
                DetailFormatter.format("1.2.3.", DetailFormatter.FormatTypeEnum.HTML));
        assertEquals(
                "a. b. c. ",
                DetailFormatter.format("a.b.c.", DetailFormatter.FormatTypeEnum.HTML));
    }

}
