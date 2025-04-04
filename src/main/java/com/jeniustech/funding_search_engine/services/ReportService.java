package com.jeniustech.funding_search_engine.services;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.LongText;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.CallNotFoundException;
import com.jeniustech.funding_search_engine.exceptions.ReportException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.CallMapper;
import com.jeniustech.funding_search_engine.repository.CallRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.util.DetailFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.jeniustech.funding_search_engine.enums.LogTypeEnum.EXPORT_PDF;

@Service
@RequiredArgsConstructor
public class ReportService extends PDFWriter {

    private final CallRepository callRepository;
    private final UserDataRepository userDataRepository;
    private final LogService logService;

    public ByteArrayInputStream generatePdf(List<Long> callIds, String subjectId, String path, String timezone, boolean hasSecretAccess) throws ReportException {
        UserData userData = userDataRepository.findBySubjectId(subjectId).orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserPDFExport(userData, logService.getCountByUserIdAndType(userData.getId(), EXPORT_PDF));

        if (callIds.isEmpty()) {
            throw new ReportException("No calls to export");
        }


        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);

            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new PageNumberHandler());

            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);
            document.setFontSize(10);
            addHeader(document, path);

            List<Call> calls = callRepository.findAllById(callIds, hasSecretAccess);
            CallMapper.sortByEndDate(calls);

            if (calls.isEmpty()) {
                throw new CallNotFoundException("Calls not found");
            }

            // add table of content
            if (calls.size() > 1) {
                document.add(new Paragraph("Table of Contents").setBold().setFontColor(PRIMARY_COLOR));
                for (Call call : calls) {
                    document.add(new Paragraph(new Text(call.getIdentifier()).setBold()).add(new Text(" - ".concat(call.getTitle())))
                            .setAction(PdfAction.createGoTo(call.getId().toString()))
                    );
                }
                newPage(document);
            }

            for (Call call : calls) {
                writeCall(document, call, timezone);
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

    private void writeCall(Document document, Call call, String timezone) {

        document.add(getTitle("Call Identifier").setDestination(call.getId().toString()));
        Rectangle rect = new Rectangle(0, 0, 523, 10);
        Link euPortalLink = new Link(call.getIdentifier(), (new PdfLinkAnnotation(rect)
                .setAction(PdfAction.createURI(call.getUrl()))));
        document.add(new Paragraph().add(euPortalLink).setFontColor(PRIMARY_COLOR).setBold().setUnderline());

        SolidBorder solidBorder = new SolidBorder(1);
        solidBorder.setColor(PRIMARY_COLOR);

        addButton(document, rect, solidBorder, "View on INNOVILYSE", call.getInnovilyseUrl());
        addButton(document, rect, solidBorder, "View on EU Portal", call.getUrl());

        addInfoField(document, call.getTitle(), "Topic");
        addInfoField(document, call.getProjectNumber(), "Number of Projects");
        addInfoField(document, call.getActionType(), "Action Type");
        addInfoField(document, call.getBudgetRangeString() + " EUR", "Budget");

        addDateRange(document, call.getStartDateDisplay(timezone), call.getEndDateDisplay(timezone), call.getEndDate2Display(timezone), "Proposal Submission Period (" + timezone + ")");

        addInfoField(document, call.getTypeOfMGADescription(), "Type of MGA");


        for (LongText longText : call.getLongTexts()) {
            addVerticalSpace(document);
            addDivider(document);
            addVerticalSpace(document);

            document.add(new Paragraph(longText.getType().getDisplayName())
                    .setBold()
                    .setFontColor(PRIMARY_COLOR));

            document.add(new Paragraph(
                    DetailFormatter.format(longText.getText(), DetailFormatter.FormatTypeEnum.TEXT)));
        }
    }

    private static void addButton(Document document, Rectangle rect, SolidBorder solidBorder, String label, String url) {
        Link detailsLink = new Link(label, (new PdfLinkAnnotation(rect)
                .setAction(PdfAction.createURI(url))));
        document.add(
                new Paragraph().add(detailsLink).setFontColor(PRIMARY_COLOR).setTextAlignment(TextAlignment.CENTER).setBold().setWidth(150)
                        .setBorder(solidBorder).setPadding(5)
        );
    }

    private void addDateRange(Document document, String startDateDisplay, String endDateDisplay, String endDate2Display, String label) {
        document.add(getTitle(label));
        Paragraph row = new Paragraph();

        if (startDateDisplay != null) {
            row.add(startDateDisplay);
        }

        if (endDateDisplay != null) {
            row.add(" > " + endDateDisplay);
        }

        if (endDate2Display != null) {
            row.add(" > " + endDate2Display);
        }

        document.add(row);
    }

    private void addInfoField(Document document, Number value, String sectionTitle) {
        if (value != null) {
            addInfoField(document, value.toString(), sectionTitle);
        }
    }

}

class PageNumberHandler implements IEventHandler {

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        int pageNumber = pdfDoc.getPageNumber(page);

        // Create a paragraph for the page number
        Paragraph pageNumberParagraph = new Paragraph(String.format("%d", pageNumber))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);

        // Position the page number in the footer (bottom center)
        float x = pdfDoc.getDefaultPageSize().getWidth() / 2;
        float y = pdfDoc.getDefaultPageSize().getBottom() + 15;

        // Add the page number at the specified location
        new Canvas(page, pdfDoc.getDefaultPageSize())
                .showTextAligned(pageNumberParagraph, x, y, TextAlignment.CENTER);
    }
}
