package com.jeniustech.funding_search_engine.mappers;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface DateMapper {

    DateTimeFormatter csvFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    DateTimeFormatter csvDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter solrFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    static LocalDateTime map(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    static String mapToSolrString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime().format(solrFormatter);
    }

    static String mapToSolrString(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().format(solrFormatter);
    }

    static Timestamp map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

    static LocalDateTime map(String date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.parse(date, csvFormatter);
    }

    static Timestamp mapToTimestamp(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(LocalDateTime.parse(date, csvFormatter));
    }

    static LocalDate mapToDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date, csvDateFormatter);
    }

    static LocalDateTime toUTC(Date date) {
        if (date == null) {
            return null;
        }
        OffsetDateTime offsetDateTime = date.toInstant().atOffset(ZoneOffset.UTC);
        ZonedDateTime utcZonedDateTime = offsetDateTime.atZoneSameInstant(ZoneOffset.UTC);
        return utcZonedDateTime.toLocalDateTime();
    }

    static String formatToDisplay(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime().format(displayFormatter);
    }

}
