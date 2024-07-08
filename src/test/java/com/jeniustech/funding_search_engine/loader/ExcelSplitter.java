package com.jeniustech.funding_search_engine.loader;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ExcelSplitter {

    private static final int ROWS_PER_FILE = 1000;
    private static int currentRowCount = 0;
    private static int fileCount = 1;
    private static Workbook currentWorkbook;
    private static Sheet currentSheet;
    private static List<String> rowData;

    private static String path = "C:/Projects/funding-search-engine/src/test/resources/data/projects/";
    private static String inputFilePath = "organization.xlsx";

    @Test
    void run() throws Exception {
        IOUtils.setByteArrayMaxOverride(1_000_000_000);

        try (OPCPackage opcPackage = OPCPackage.open(new FileInputStream(path + inputFilePath))) {
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

            while (sheets.hasNext()) {
                try (InputStream sheetStream = sheets.next()) {
                    processSheet(sheetStream);
                }
            }
        }
    }

    private static void processSheet(InputStream sheetStream) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        InputSource sheetSource = new InputSource(sheetStream);
        saxParser.parse(sheetSource, new SheetHandler());
    }

    private static void startNewWorkbook() throws IOException {
        if (currentWorkbook != null) {
            String outputFilePath = path + "split/" + inputFilePath.replace(".xlsx", "_" + fileCount++ + ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                currentWorkbook.write(fos);
            }
            currentWorkbook.close();
        }
        currentWorkbook = new XSSFWorkbook();
        currentSheet = currentWorkbook.createSheet("Sheet1");
        currentRowCount = 0;
    }

    private static void addRowDataToSheet(List<String> rowData) {
        Row newRow = currentSheet.createRow(currentRowCount++);
        int cellIndex = 0;
        for (String cellValue : rowData) {
            Cell newCell = newRow.createCell(cellIndex++);
            newCell.setCellValue(cellValue);
        }
    }

    private static class SheetHandler extends DefaultHandler {
        private boolean isCellOpen = false;

        public SheetHandler() throws IOException {
            startNewWorkbook();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("row".equals(qName)) {
                rowData = new ArrayList<>();
            } else if ("c".equals(qName)) {
                isCellOpen = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("v".equals(qName)) {
                isCellOpen = false;
            } else if ("c".equals(qName)) {
                isCellOpen = false;
            } else if ("row".equals(qName)) {
                addRowDataToSheet(rowData);
                if (currentRowCount % ROWS_PER_FILE == 0) {
                    try {
                        startNewWorkbook();
                    } catch (IOException e) {
                        throw new SAXException("Error creating new workbook", e);
                    }
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isCellOpen) {
                rowData.add(new String(ch, start, length));
            }
        }
    }
}
