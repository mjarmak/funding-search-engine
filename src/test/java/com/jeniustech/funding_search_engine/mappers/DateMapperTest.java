package com.jeniustech.funding_search_engine.mappers;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateMapperTest {

    DateTimeFormatter solrFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXX");

    @Test
    void getLocalDateTimeTest() {
        LocalDateTime localDateTime = DateMapper.map("2014-08-28T17:01:02");
        assertEquals("2014-08-28T17:01:02", localDateTime.toString());
        assertEquals(2014, localDateTime.getYear());
        assertEquals(8, localDateTime.getMonthValue());
        assertEquals(28, localDateTime.getDayOfMonth());
        assertEquals(17, localDateTime.getHour());
        assertEquals(1, localDateTime.getMinute());
        assertEquals(2, localDateTime.getSecond());
    }

    @Test
    void toUTC() {
        Date date = toDate("2014-08-28T19:01:02.003+0200");
        LocalDateTime localDateTime = DateMapper.toUTC(date);
        assertEquals(2014, localDateTime.getYear());
        assertEquals(8, localDateTime.getMonthValue());
        assertEquals(28, localDateTime.getDayOfMonth());
        assertEquals(17, localDateTime.getHour());
        assertEquals(1, localDateTime.getMinute());
        assertEquals(2, localDateTime.getSecond());

    }

    Date toDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(LocalDateTime.parse(date, solrFormat));
    }

}
