package com.jeniustech.funding_search_engine.services;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.jeniustech.funding_search_engine.entities.BusinessInformation;
import com.jeniustech.funding_search_engine.entities.Payment;
import com.jeniustech.funding_search_engine.exceptions.InvoiceException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
public class InvoiceService extends PDFWriter {

    public static final int DUE_DAYS = 30;
    public static final int TABLE_WIDTH = 512;

    public static final String JENIUS_TECH_SRL = "Jenius Tech SRL";
    public static final String CONTACT_INNOVILYSE_COM = "contact@innovilyse.com";
    public static final String VAT_NUMBER = "BE0789424602";
    public static final String ADDRESS_LINE_1 = "Avenue des Volontaires 38, 1040";
    public static final String ADDRESS_LINE_2 = "Brussels, Belgium";

    @Value("${invoice.vat}")
    private BigDecimal vat;

    public ByteArrayInputStream generatePdf(Payment payment) throws InvoiceException {
        if (payment.getUserData() == null) {
            throw new InvoiceException("User data not found");
        }

        String issueDate = payment.getCreatedAtDisplayDate();
        String dueDate = DateMapper.formatToDisplay(DateMapper.map(payment.getCreatedAt()).toLocalDate().plusDays(DUE_DAYS));
        String currency = payment.getCurrencyDisplay();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);
            document.setFontSize(10);
            addHeader(document);

            // Add header
            Table header = new Table(new float[]{1, 1});
            header.setWidth(TABLE_WIDTH);

            // Add business information
            Cell left = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
            left.add(new Paragraph(JENIUS_TECH_SRL).setBold());
            left.add(new Paragraph(CONTACT_INNOVILYSE_COM));
            left.add(new Paragraph(VAT_NUMBER));
            left.add(new Paragraph(ADDRESS_LINE_1));
            left.add(new Paragraph(ADDRESS_LINE_2));

            header.addCell(left);

            // Add customer information
            Cell right = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);

            BusinessInformation businessInformation = payment.getUserData().getBusinessInformation();
            if (businessInformation != null) {
                if (StringUtil.isNotEmpty(businessInformation.getName())) {
                    right.add(new Paragraph(businessInformation.getName()).setBold());
                }
                if (StringUtil.isNotEmpty(businessInformation.getEmail())) {
                    right.add(new Paragraph(businessInformation.getEmail()));
                }
                if (StringUtil.isNotEmpty(businessInformation.getVatNumber())) {
                    right.add(new Paragraph(businessInformation.getVatNumber()));
                }
                if (StringUtil.isNotEmpty(businessInformation.getPhoneNumber())) {
                    right.add(new Paragraph(businessInformation.getPhoneNumber()));
                }
                if (businessInformation.getAddress() != null) {
                    String addressLine = businessInformation.getAddress().getStreet();
                    if (StringUtil.isNotEmpty(businessInformation.getAddress().getPostCode())) {
                        addressLine += ", " + businessInformation.getAddress().getPostCode();
                    }
                    if (StringUtil.isNotEmpty(addressLine)) {
                        right.add(new Paragraph(addressLine));
                    }
                    String cityLine = businessInformation.getAddress().getCity();
                    if (StringUtil.isNotEmpty(businessInformation.getAddress().getCountry())) {
                        cityLine += ", " + businessInformation.getAddress().getCountry().getDisplayName();
                    }
                    if (StringUtil.isNotEmpty(cityLine)) {
                        right.add(new Paragraph(cityLine));
                    }
                }
            }

            header.addCell(right);

            document.add(header);

            addVerticalSpace(document);

            // Add payment information
            addInfoField(document, payment.getInvoiceId(), "Invoice ID");
            addInfoField(document, issueDate, "Invoice Issue Date");
            addInfoField(document, dueDate, "Due Date");

            addVerticalSpace(document);
            addDivider(document, 2);
            addVerticalSpace(document);
            addVerticalSpace(document);

            Table products = new Table(new float[]{1, 1, 1, 1});
            addProductHeaders(currency, products);
            addProducts(payment, products);
            addProductsTotal(payment, currency, products);
            document.add(products);

            addVerticalSpace(document);
            addVerticalSpace(document);
            addDivider(document, 2);
            addVerticalSpace(document);

            // Add payment instructions
            document.add(getTitle("Payment Instructions").setFontSize(20));
            addInfoField(document, payment.getAmount().stripTrailingZeros().toString(), "Amount (" + currency + ")");
            addInfoField(document, JENIUS_TECH_SRL, "Recipient");
            addInfoField(document, "BE90 3632 2405 9532", "IBAN");
            addInfoField(document, "BBRUBEBB", "BIC");
            addInfoField(document, payment.getCommunicationMessage(), "Communication Message");

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new InvoiceException(e.getMessage());
        }

    }

    private void addProductHeaders(String currency, Table productsHeaders) {
        productsHeaders.setWidth(TABLE_WIDTH);
        addTableCellBorderBottom(productsHeaders, "Product", TextAlignment.LEFT);
        addTableCellBorderBottom(productsHeaders, "Quantity", TextAlignment.CENTER);
        addTableCellBorderBottom(productsHeaders, "Total (excl. VAT) (" + currency + ")", TextAlignment.CENTER);
        addTableCellBorderBottom(productsHeaders, "VAT (" + vat + "%) (" + currency + ")", TextAlignment.RIGHT);
    }

    private void addProducts(Payment payment, Table products) {
        products.setWidth(TABLE_WIDTH);
        addTableCell(products, "Subscription", TextAlignment.LEFT);
        addTableCell(products, "1", TextAlignment.CENTER);
        addTableCell(products, payment.getAmountExcludingVAT(vat).toString(), TextAlignment.CENTER);
        addTableCell(products, payment.getVAT(vat).toString(), TextAlignment.RIGHT);
    }

    private static void addProductsTotal(Payment payment, String currency, Table productsTotal) {
        productsTotal.setWidth(TABLE_WIDTH);
        addTableCell(productsTotal, "", TextAlignment.LEFT);
        addTableCell(productsTotal, "", TextAlignment.LEFT);
        addTableCellBold(productsTotal, "Total (incl. VAT) (" + currency + ")", TextAlignment.RIGHT);
        addTableCellBoldBorderTop(productsTotal, payment.getAmount().toString(), TextAlignment.RIGHT);
    }

    private static void addTableCell(Table header, String Jenius_Tech_SRL, TextAlignment left) {
        header.addCell(new Cell().add(new Paragraph(Jenius_Tech_SRL)).setBorder(Border.NO_BORDER).setTextAlignment(left));
    }

    private static void addTableCellBold(Table header, String Jenius_Tech_SRL, TextAlignment left) {
        header.addCell(new Cell().add(new Paragraph(Jenius_Tech_SRL)).setBorder(Border.NO_BORDER).setTextAlignment(left).setBold());
    }
    private static void addTableCellBoldBorderTop(Table header, String Jenius_Tech_SRL, TextAlignment left) {
        header.addCell(new Cell().add(new Paragraph(Jenius_Tech_SRL)).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(1)).setTextAlignment(left).setBold());
    }

    private static void addTableCellBorderBottom(Table header, String Jenius_Tech_SRL, TextAlignment left) {
        header.addCell(new Cell().add(new Paragraph(Jenius_Tech_SRL)).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(1)).setTextAlignment(left).setBold());
    }
}
