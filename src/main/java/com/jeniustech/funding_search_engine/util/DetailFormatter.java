package com.jeniustech.funding_search_engine.util;

public interface DetailFormatter {

    enum FormatTypeEnum {
        HTML, TEXT
    }

    static String format(String detail, FormatTypeEnum formatType) {
        if (detail == null) {
            return null;
        }
        if (formatType == FormatTypeEnum.TEXT) {
            detail = detail
                    // extract links
                    .replaceAll("<a[^>]*href=\"([^\"]*)\"[^>]*>([^<]*)</a>", "$2 ($1)")

                    .replaceAll("<br>", "\n\n")
                    .replaceAll("<p>", "\n")
                    .replaceAll("</p>", "\n\n")

                    // add dot
                    .replaceAll("<li>", "\n\t• ")

                    .replaceAll("<ul>", "\n\n")
                    .replaceAll("</ul>", "\n\n")
                    .replaceAll("<[^>]*>", "")
                    .replaceAll("\n \n", "\n\n")
                    .replaceAll("\n\n\n\n", "\n\n")
                    .replaceAll("\n\n\n", "\n\n")
                    .trim();
        }
        return detail;
    }
}
