package com.jeniustech.funding_search_engine.util;

import org.junit.jupiter.api.Test;

import static com.jeniustech.funding_search_engine.util.QueryUtil.splitQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryUtilTest {

    @Test
    void splitQueryTest() {
        assertEquals(1, splitQuery("test").length);
        assertEquals(2, splitQuery("test query").length);
        assertEquals(3, splitQuery("test query example").length);
        assertEquals(3, splitQuery("test;query;example").length);
        assertEquals(3, splitQuery("test,query;example").length);
        assertEquals(3, splitQuery("test,query example").length);
        assertEquals(3, splitQuery("test;query example").length);
    }
}
