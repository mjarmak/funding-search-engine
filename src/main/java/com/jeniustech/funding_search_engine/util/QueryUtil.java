package com.jeniustech.funding_search_engine.util;

import jakarta.validation.constraints.NotNull;

public interface QueryUtil {

    Float MIN_SCORE = 1F;

    static String[] splitQuery(String query) {
        // split by space, semicolon, and comma
        return query.replaceAll("[\\s;]", ",").replaceAll(",", " ").split(" ");
    }

    @NotNull
    static Float getMinScore(String query) {
        if (query == null || query.isBlank()) {
            return MIN_SCORE;
        } else if (splitQuery(query).length == 1) {
            return 0.5F;
        }

        return MIN_SCORE;
    }

}
