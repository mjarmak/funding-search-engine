package com.jeniustech.funding_search_engine.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.util.DetailFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.jeniustech.funding_search_engine.enums.LogTypeEnum.EXPORT_PDF;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CallRepository callRepository;
    private final UserDataRepository userDataRepository;
    private final LogService logService;
    static final int paddingSmall = 10;

    public ByteArrayInputStream generatePdf(Long callId, String subjectId) {

        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserPDFExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_PDF));

        Call call = callRepository.findById(callId).orElseThrow(() -> new CallNotFoundException("Call not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            addHeader(document);

            document.add(getTitle("Call Identifier"));
            Rectangle rect = new Rectangle(0, 0, 523, 10);
            Link link = new Link(call.getIdentifier(), (new PdfLinkAnnotation(rect)
                    .setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT)
                    .setAction(PdfAction.createURI(call.getUrl())
                    )));
            document.add(new Paragraph().add(link.setUnderline()));


            addInfoField(document, call.getTitle(), "Topic");
            addInfoField(document, call.getProjectNumber().toString(), "Number of Projects");
            addInfoField(document, call.getActionType(), "Action Type");
            addInfoField(document, call.getBudgetRangeString() + " EUR", "Budget");

            addDateRange(document, call.getStartDateDisplay(), call.getEndDateDisplay(), call.getEndDate2Display(), "Proposal Submission Period");

            addInfoField(document, call.getTypeOfMGADescription(), "Type of MGA");


            for (LongText longText : call.getLongTexts()) {
                addVerticalSpace(document);
                addDivider(document);
                addVerticalSpace(document);

                document.add(new Paragraph(longText.getType().getDisplayName())
                        .setBold()
                        .setFontColor(new DeviceRgb(78, 85, 243)));

                document.add(new Paragraph(
                        DetailFormatter.format(longText.getText(), DetailFormatter.FormatTypeEnum.TEXT)));
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logService.addLog(userData, EXPORT_PDF, callId.toString());

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addDateRange(Document document, String startDateDisplay, String endDateDisplay, String endDate2Display, String proposalSubmissionPeriod) {
        document.add(getTitle(proposalSubmissionPeriod));
        Paragraph row = new Paragraph()
                .add(startDateDisplay);

        if (endDateDisplay != null) {
            row.add(" > " + endDateDisplay);
        }

        if (endDate2Display != null) {
            row.add(" > " + endDate2Display);
        }

        document.add(row);
    }

    private static void addVerticalSpace(Document document) {
        document.add(new Paragraph().setHeight(paddingSmall));
    }

    private static void addDivider(Document document) {
        addDivider(document, 1);
    }

    private static void addDivider(Document document, int width) {
        document.add(new LineSeparator(new SolidLine(width)));
    }

    private static void addHeader(Document document) throws IOException {
        ClassPathResource resource = new ClassPathResource("img/logo-floating-512x512.png");

        String largeText = "Innovilyse";
        Paragraph paragraph = new Paragraph(largeText)
                .setFontSize(50)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT);

        // Load image from resources
        Image img = new Image(ImageDataFactory.create(resource.getFile().getPath()))
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

    private static void addInfoField(Document document, String value, String sectionTitle) {
        if (value != null) {
            document.add(getTitle(sectionTitle));
            document.add(new Paragraph(value));
        }
    }

    private static Paragraph getTitle(String sectionTitle) {
        return new Paragraph(sectionTitle)
                .setBold()
                .setMarginBottom(0)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.LEFT);
    }

}
