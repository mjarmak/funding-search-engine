package com.jeniustech.funding_search_engine.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {

    public static String readFile(String path) throws IOException {
        return Files.readAllLines(new ClassPathResource(path).getFile().toPath()).stream().reduce("", String::concat);
    }

}
