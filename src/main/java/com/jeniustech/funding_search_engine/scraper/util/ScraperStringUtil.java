package com.jeniustech.funding_search_engine.scraper.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.jeniustech.funding_search_engine.mappers.DateMapper.csvFormatter;
import static com.jeniustech.funding_search_engine.util.StringUtil.removeMultiSpaces;

public class ScraperStringUtil {

    public static final DateTimeFormatter solrCSVFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXX");

    public static String processString(Object str, boolean isDateString) {
        if (str == null) {
            return "";
        }
        if (isDateString) {
            return toUTC((String) str);
        }
        if (str instanceof String) {
            String out;
            out = ((String) str).trim();

//            out = replaceSpecialHtmlCharacters(out);
//            out = removeHtmlTags(out);

//            out = encode(out);
//            out = replaceStrangeCharacters(out);
//            out = replaceSpecialCharacters(out);
            out = removeMultiSpaces(out);
            out = (out).replace("\"", "\"\"");
            if ((out).contains(",") || (out).contains("\n")) {
                out = "\"" + out + "\"";
            }

            out = nullIfEmpty(out);

            return out;
        }
        return str.toString();
    }

    private static String replaceSpecialCharacters(String out) {
        out = out.replaceAll("[ÄÅÂÁÀ]", "A");
        out = out.replaceAll("[âäáàã]", "a");
        out = out.replaceAll("Æ", "AE");
        out = out.replaceAll("æ", "ae");
        out = out.replaceAll("ß", "SS");
        out = out.replaceAll("Ç", "C");
        out = out.replaceAll("ç", "c");
        out = out.replaceAll("[ÎÏÍÌ]", "I");
        out = out.replaceAll("[ïîìí]", "i");
        out = out.replaceAll("[ÊËÉÈ]", "E");
        out = out.replaceAll("[êëéè]", "e");
        out = out.replaceAll("[ÛÜÚÙ]", "U");
        out = out.replaceAll("[üûúù]", "u");
        out = out.replaceAll("[ÔÖÓÒ]", "O");
        out = out.replaceAll("[öôóò]", "o");
        out = out.replaceAll("Ñ", "N");
        out = out.replaceAll("ñ", "n");
        out = out.replaceAll("[ÝŸ]", "Y");
        out = out.replaceAll("[ýÿ]", "y");
        return out;
    }

    private static String nullIfEmpty(String out) {
        if (out.equals("")) {
            out = null;
        } else if (out.isEmpty() || out.isBlank()) {
            out = null;
        } else if (out.equals("null")) {
            out = null;
        } else if (out.equals("-")) {
            out = null;
        }
        return out;
    }

    private static String encode(String out) {
        byte[] bytes = out.getBytes(StandardCharsets.ISO_8859_1);
        out = new String(bytes, StandardCharsets.UTF_8);
        return out;
    }

    private static String replaceStrangeCharacters(String out) {
        out = out.replace("â€™", "'");
        out = out.replace("â‚¬", "€");
        out = out.replace("Â£", "£");
        out = out.replace("a€™", "'");
        out = out.replace("Â¥", "¥");
        out = out.replace("Â©", "©");
        out = out.replace("Â®", "®");
        out = out.replace("â„¢", "™");
        out = out.replace("Â§", "§");
        out = out.replace("Â°", "°");
        out = out.replace("Â±", "±");
        out = out.replace("Âµ", "µ");
        out = out.replace("Â¶", "¶");
        out = out.replace("Â·", "·");
        out = out.replace("â€¢", "•");
        out = out.replace("Ãª", "•");
        out = out.replace("â€¦", "…");
        out = out.replace("â€”", "—");
        out = out.replace("â€“", "–");
        out = out.replace("â€œ", "“");
        out = out.replace("â€", "”");
        out = out.replace("â€˜", "'");
        out = out.replace("Â«", "«");
        out = out.replace("Â»", "»");
        out = out.replace("Ã³", "");
        out = out.replace("Ã©", "");
        out = out.replace("Ã¨", "");
        out = out.replace("Â", "");

        out = out.replace("’", "'");
        out = out.replace("‘", "'");
        out = out.replace("–", "-");
        out = out.replace("“", "'");
        out = out.replace("\u00a0", " ");

        return out;
    }

    private static String replaceSpecialHtmlCharacters(String out) {
        out = out.replace("&nbsp;", " ");
        out = out.replace("&mdash;", "-");
        out = out.replace("&ndash;", "-");
        out = out.replace("&amp;", "&");
        out = out.replace("&quot;", "\"");
        out = out.replace("&lt;", "<");
        out = out.replace("&gt;", ">");
        out = out.replace("&euro;", "€");
        out = out.replace("&pound;", "£");
        out = out.replace("&yen;", "¥");
        out = out.replace("&copy;", "©");
        out = out.replace("&reg;", "®");
        out = out.replace("&trade;", "™");
        out = out.replace("&sect;", "§");
        out = out.replace("&deg;", "°");
        out = out.replace("&plusmn;", "±");
        out = out.replace("&micro;", "µ");
        out = out.replace("&para;", "¶");
        out = out.replace("&middot;", "·");
        out = out.replace("&bull;", "•");
        out = out.replace("&hellip;", "…");
        out = out.replace("&mdash;", "—");
        out = out.replace("&ndash;", "–");
        out = out.replace("&ldquo;", "“");
        out = out.replace("&rdquo;", "”");
        out = out.replace("&lsquo;", "'");
        out = out.replace("&rsquo;", "’");
        out = out.replace("&laquo;", "«");
        out = out.replace("&raquo;", "»");
        return out;
    }

    public static String toUTC(String date) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, formatter);
        ZonedDateTime utcZonedDateTime = offsetDateTime.atZoneSameInstant(ZoneOffset.UTC);
        return utcZonedDateTime.format(csvFormatter);
    }

    public static String removeHtmlTags(String str) {
        return str.replaceAll("<[^>]*>", "");
    }

    public static LocalDateTime getLocalDateTime(String date) {
        String utc = toUTC(date);
        return LocalDateTime.parse(utc, csvFormatter);
    }

}
