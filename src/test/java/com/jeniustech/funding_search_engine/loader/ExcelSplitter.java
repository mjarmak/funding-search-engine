package com.jeniustech.funding_search_engine.loader;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

public class ExcelSplitter {
    private static final int ROWS_PER_FILE = 1000;
    private static int currentRowCount = 0;
    private static int fileCount = 1;
    private static Workbook currentWorkbook;
    private static Sheet currentSheet;
    private static List<String> rowData;
    private static List<String> headerRowData;

    private static String path = "C:/Projects/funding-search-engine/src/test/resources/data/projects/";
    private static String inputFilePath = "organization.xlsx";

//    @Test
    void run() throws Exception {
        IOUtils.setByteArrayMaxOverride(1_000_000_000);

        try (OPCPackage opcPackage = OPCPackage.open(new FileInputStream(path + inputFilePath))) {
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

            while (sheets.hasNext()) {
                try (InputStream sheetStream = sheets.next()) {
                    processSheet(sheetStream);

                    // Save the last workbook
                    saveCurrentWorkbook();
                }
            }
        }
        System.out.println("Wrote " + fileCount + " files");
        System.out.println("Wrote " + currentRowCount + " rows");
    }

    private static void processSheet(InputStream sheetStream) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        InputSource sheetSource = new InputSource(sheetStream);
        saxParser.parse(sheetSource, new SheetHandler());
    }

    private static void startNewWorkbook() throws IOException {
        saveCurrentWorkbook();

        currentWorkbook = new XSSFWorkbook();
        currentSheet = currentWorkbook.createSheet("DATA");
        currentRowCount = 0;

        // Add header row to the new workbook
        if (headerRowData != null) {
            addRowDataToSheet(headerRowData);
        }
    }

    private static void saveCurrentWorkbook() throws IOException {
        if (currentWorkbook != null) {
            String outputFilePath = path + "split/" + inputFilePath.replace(".xlsx", "_" + fileCount++ + ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                currentWorkbook.write(fos);
            }
            currentWorkbook.close();
        }
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
        private String currentCellReference;
        private int lastColumnNumber = -1;

        public SheetHandler() throws IOException {
            startNewWorkbook();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("row".equals(qName)) {
                rowData = new ArrayList<>();
                lastColumnNumber = -1;
            } else if ("c".equals(qName)) {
                isCellOpen = true;
                currentCellReference = attributes.getValue("r");
                int currentColumn = getColumnIndex(currentCellReference);

                // Fill in missing cells
                for (int i = lastColumnNumber + 1; i < currentColumn; i++) {
                    rowData.add("");
                }
                lastColumnNumber = currentColumn;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("v".equals(qName)) {
                isCellOpen = false;
            } else if ("c".equals(qName)) {
                isCellOpen = false;
                // Ensure each cell ends up in the correct column position
                if (rowData.size() <= lastColumnNumber) {
                    rowData.add("");
                }
            } else if ("row".equals(qName)) {
                if (currentRowCount == 0 && headerRowData == null) {
                    // Store header row data
                    headerRowData = new ArrayList<>(rowData);
                }
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
                String cellValue = new String(ch, start, length);
                if (rowData.size() <= lastColumnNumber) {
                    rowData.add(cellValue);
                } else {
                    rowData.set(lastColumnNumber, rowData.get(lastColumnNumber) + cellValue);
                }
            }
        }

        private int getColumnIndex(String cellReference) {
            int columnIndex = -1;
            for (int i = 0; i < cellReference.length(); i++) {
                if (Character.isDigit(cellReference.charAt(i))) {
                    break;
                }
                int thisChar = cellReference.charAt(i) - 'A' + 1;
                columnIndex = (columnIndex + 1) * 26 + thisChar;
            }
            return columnIndex - 1;
        }
    }
}
