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
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.ReportException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.util.DetailFormatter;
import com.jeniustech.funding_search_engine.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.jeniustech.funding_search_engine.enums.LogTypeEnum.EXPORT_PDF;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CallRepository callRepository;
    private final UserDataRepository userDataRepository;
    private final LogService logService;
    static final int paddingSmall = 10;

    public ByteArrayInputStream generatePdf(List<Long> callIds, String subjectId, String path) throws ReportException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserPDFExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_PDF));

        if (callIds.isEmpty()) {
            throw new ReportException("No calls to export");
        }


        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            addHeader(document, path);

            List<Call> calls = callRepository.findAllById(callIds);
            if (calls.isEmpty()) {
                throw new CallNotFoundException("Calls not found");
            }
            for (Call call : calls) {
                writeCall(document, call);
                if (calls.indexOf(call) != calls.size() - 1) {
                    newPage(document);
                }
            }

            logService.addLog(userData, EXPORT_PDF, callIds.toString().substring(1, Math.min(callIds.toString().length(), 254)));

            writer.close();
            pdfDoc.close();
            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new ReportException("Error generating PDF");
        }
    }

    private void newPage(Document document) {
        document.add(new AreaBreak());
    }

    private void writeCall(Document document, Call call) {

        document.add(getTitle("Call Identifier"));
        Rectangle rect = new Rectangle(0, 0, 523, 10);
        Link link = new Link(call.getIdentifier(), (new PdfLinkAnnotation(rect)
                .setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT)
                .setAction(PdfAction.createURI(call.getUrl())
                )));
        document.add(new Paragraph().add(link.setUnderline()));


        addInfoField(document, call.getTitle(), "Topic");
        addInfoField(document, call.getProjectNumber(), "Number of Projects");
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

    private static void addHeader(Document document, String path) throws IOException {
        String largeText = "Innovilyse";
        Paragraph paragraph = new Paragraph(largeText)
                .setFontSize(50)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT);

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

    private static void addInfoField(Document document, Number value, String sectionTitle) {
        if (value != null) {
            document.add(getTitle(sectionTitle));
            document.add(new Paragraph(value.toString()));
        }
    }

    private static void addInfoField(Document document, String value, String sectionTitle) {
        if (StringUtil.isNotEmpty(value)) {
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
