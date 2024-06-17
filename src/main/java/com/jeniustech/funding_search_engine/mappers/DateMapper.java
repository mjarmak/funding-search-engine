package com.jeniustech.funding_search_engine.mappers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface DateMapper {

    DateTimeFormatter csvFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    static LocalDateTime map(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    static Timestamp map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

    static LocalDateTime map(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime()).toLocalDateTime();
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

}
