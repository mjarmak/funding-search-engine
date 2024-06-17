package com.jeniustech.funding_search_engine.mappers;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateMapperTest {

    @Test
    void getLocalDateTimeTest() {
        LocalDateTime localDateTime = DateMapper.map("2014-08-28T17:01:02");
        assertEquals("2014-08-28T17:00:00", localDateTime.toString());
        assertEquals(2014, localDateTime.getYear());
        assertEquals(8, localDateTime.getMonthValue());
        assertEquals(28, localDateTime.getDayOfMonth());
        assertEquals(17, localDateTime.getHour());
        assertEquals(1, localDateTime.getMinute());
        assertEquals(2, localDateTime.getSecond());


    }

}
