package com.jeniustech.funding_search_engine.scraper.util;

import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVSplitter {
    private static final int ROWS_PER_FILE = 50000;

    public static List<String> splitCSVFile(String fileName) throws ScraperException {
        List<String> splitFileNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String headers = reader.readLine();
            String line = reader.readLine();
            int fileCount = 1;
            while (line != null) {

                String splitFileName = fileName.replace(".csv", "") + "_split_" + fileCount + ".csv";
                try (PrintWriter writer = new PrintWriter(new FileWriter(splitFileName))) {
                    writer.println(headers);
                    int rowCount = 0;
                    while (line != null && rowCount < ROWS_PER_FILE) {
                        writer.println(line);
                        line = reader.readLine();
                        rowCount++;
                    }
                }

                splitFileNames.add(splitFileName);
                fileCount++;
            }
        } catch (IOException e) {
            log.error("Error splitting CSV file", e);
            e.printStackTrace();
            throw new ScraperException("Error splitting CSV file");
        }
        log.info("Split {} into {} files", fileName, splitFileNames.size());
        return splitFileNames;
    }
}
