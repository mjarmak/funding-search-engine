package com.jeniustech.funding_search_engine.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.jeniustech.funding_search_engine.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class PDFWriter {

    public static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(78, 85, 243);
    static final int paddingSmall = 10;

    @Value("${pdf.logo.url}")
    private String logoUrl;

    @Value("${ui.url}")
    private String uiUrl;

    void addHeader(Document document) throws IOException {
        addHeader(document, null);
    }

    void addHeader(Document document, String path) throws IOException {
        if (StringUtil.isEmpty(path)) {
            path = logoUrl;
        }

        String largeText = "INNOVILYSE";
        Rectangle rect = new Rectangle(0, 0, 523, 10);
        Link link = new Link(largeText, (new PdfLinkAnnotation(rect)
                .setAction(PdfAction.createURI(uiUrl))
        ));
        Paragraph paragraph = new Paragraph()
                .setFontSize(50)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .add(link);

        // Load image from resources
        Image img = new Image(ImageDataFactory.create(path))
                .setWidth(50)
                .setHeight(50);

        Paragraph imageParagraph = new Paragraph()
                .add(img)
                .setMarginRight(10);

        // Create a paragraph to hold both text and image
        Paragraph row = new Paragraph()
                .add(imageParagraph)
                .add(paragraph)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(row);
        addDivider(document, 5);
        addVerticalSpace(document); // Vertical space of 20 points
    }


    void addVerticalSpace(Document document) {
        document.add(new Paragraph().setHeight(paddingSmall));
    }

    void addDivider(Document document) {
        addDivider(document, 1);
    }

    void addDivider(Document document, int width) {
        document.add(new LineSeparator(new SolidLine(width)));
    }

    void addInfoField(Document document, String value, String sectionTitle) {
        if (StringUtil.isNotEmpty(value)) {
            document.add(getTitle(sectionTitle));
            document.add(new Paragraph(value));
        }
    }

    Paragraph getTitle(String sectionTitle) {
        return new Paragraph(sectionTitle)
                .setBold()
                .setMarginBottom(0)
//                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.LEFT);
    }

    void newPage(Document document) {
        document.add(new AreaBreak());
    }
}
