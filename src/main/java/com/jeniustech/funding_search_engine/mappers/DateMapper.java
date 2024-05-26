package com.jeniustech.funding_search_engine.mappers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class DateMapper {

    public static LocalDateTime map(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    public static Timestamp map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

}
