package com.jeniustech.funding_search_engine.scraper.util;

import com.jeniustech.funding_search_engine.util.StringUtil;

import java.nio.charset.StandardCharsets;

public class ScraperStringUtil {


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

    public static String encode(String out) {
        byte[] bytes = out.getBytes(StandardCharsets.ISO_8859_1);
        out = new String(bytes, StandardCharsets.UTF_8);
        return out;
    }

    public static String replaceStrangeCharacters(String out) {
        out = out.replace("’", "'");
        out = out.replace("‘", "'");
        out = out.replace("–", "-");
        out = out.replace("“", "'");
        out = out.replace("\u00a0", " ");

        return out;
    }

    public static String removeUselessHtmlData(String text) {
        text = text.replaceAll("\\s+((?!href)[^=\\s]+)=\"[^\"]*\"", "");
        text = text.replaceAll("target=_self", "");

        text = text.replaceAll("<a[^>]*href=#.*?\\d+[^>]*>.*?</a>", "");
        text = text.replaceAll("<a[^>]*href=\"#.*?\\d+\"[^>]*>.*?</a>", "");
        text = text.replaceAll("\\s+id=[^\\s>]*", "");
        text = text.replaceAll("<sup></sup>", "");

        text = text.replaceAll("href=\"", "target=\"_blank\" href=\"");
        text = text.replaceAll("<p >", "<p>");
        text = text.replaceAll("> <", "><");
        text = text.replaceAll("<SPAN>", "<span>");
        text = text.replaceAll("<SPAN >", "<span>");
        text = text.replaceAll("<span >", "<span>");
        text = text.replaceAll("</SPAN>", "</span>");
        text = text.replaceAll("<p></p>", "");
        text = text.replaceAll("<p><br></p>", "");
        return text;
    }

    public static String removeHtmlTags(String str) {
        return StringUtil.removeMultiSpaces(str.replaceAll("<[^>]*>", " ")).trim();
    }


}
