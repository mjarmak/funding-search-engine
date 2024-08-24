package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${ui.url}")
    private String uiUrl;

    private final JavaMailSender mailSender;

    public void sendNewCallsNotification(SavedSearch savedSearch, List<CallDTO> callDTOS) {
        if (callDTOS.isEmpty()) {
            return;
        }
        String subject = callDTOS.size() + " New Calls Available on INNOVILYSE for \"" + savedSearch.getName() + "\"";
        String text = getEmailBody(subject, callDTOS, savedSearch);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(from, "INNOVILYSE"));
            helper.setReplyTo(from);
            helper.setTo(savedSearch.getUserData().getEmail());
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (MailSendException | MessagingException | UnsupportedEncodingException e) {
            throw new ScraperException(e.getMessage());
        }
    }

    private String getEmailBody(String subject, List<CallDTO> callDTOS, SavedSearch savedSearch) {
        // as html
        String noWrapCell = "<td style=\"white-space: nowrap\">";

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>");
        builder.append("<head><style>table {width: 100%; border: 1px solid lightgrey; padding: 4px; border-radius: 8px; border-collapse: collapse;} ");
        builder.append("th, td {border: 1px solid lightgrey; padding: 4px; text-align: left;}</style></head>");
        builder.append("<body>");
        builder.append("<h1 style=\"text-align:center\">")
        .append(subject)
        .append("</h1>");
        builder.append("<table>");
        builder.append("<tr><th>Call Info</th><th>Proposal Submission Period (UTC)</th><th>Budget (EUR)</th></tr>");
        for (CallDTO callDTO : callDTOS) {
            String callUrl = UrlTypeEnum.getInnovilyseUrl("call", callDTO.getId());
            String submissionPeriod = "From: " + DateMapper.formatToDisplay(callDTO.getStartDate()) + "<br>To: " + DateMapper.formatToDisplay(callDTO.getEndDate());
            if (callDTO.getEndDate2() != null) {
                submissionPeriod += "<br>Then to: " + DateMapper.formatToDisplay(callDTO.getEndDate2());
            }

            String budgetRangeString;
            if (callDTO.getBudgetMin() != null) {
                budgetRangeString = callDTO.getBudgetMin();
                if (callDTO.getBudgetMax() != null && !callDTO.getBudgetMax().equals(callDTO.getBudgetMin())) {
                    budgetRangeString += " - " + callDTO.getBudgetMax();
                }
            } else if (callDTO.getBudgetMax() != null) {
                budgetRangeString = "< " + callDTO.getBudgetMax();
            } else {
                budgetRangeString = "N/A";
            }

            builder.append("<tr>");
            builder.append("<td>")
                    .append("<a href=\"").append(callUrl).append("\"><b>")
                    .append(callDTO.getIdentifier()).append("</b><br>")
                    .append(callDTO.getTitle()).append("</a></td>");
            builder.append(noWrapCell).append(submissionPeriod).append("</td>");
            builder.append(noWrapCell).append(budgetRangeString).append("</td>");
            builder.append("</tr>");
        }
        builder.append("</table>");
        String seeMoreUrl = uiUrl + "/search?query=" + savedSearch.getValue() + "&statusFilters=UPCOMING,OPEN";
        builder.append("<h4 style=\"text-align:center\">");
//        builder.append("<a href=\"").append(seeMoreUrl).append("\">Search more</a></h4>");
        builder.append("</body></html>");

        return builder.toString();
    }
}
