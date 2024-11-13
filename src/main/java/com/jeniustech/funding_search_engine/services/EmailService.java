package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.entities.Payment;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.enums.UrlTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.EmailServiceException;
import com.jeniustech.funding_search_engine.exceptions.InvoiceException;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    public static final String PRIMARY_COLOR = "#4E55F3";

    @Value("${spring.mail.username}")
    private String from;

    @Value("${ui.url}")
    private String uiUrl;

    private final JavaMailSender mailSender;

    private final InvoiceService invoiceService;

    private void sendEmail(String to, String subject, String body, String fileName, ByteArrayInputStream pdf) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(from, "INNOVILYSE"));
            helper.setReplyTo(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            if (fileName != null && pdf != null) {
                DataSource source = new ByteArrayDataSource(pdf, "application/pdf");
                helper.addAttachment(fileName, source);
            }
            mailSender.send(message);
        } catch (MailSendException | MessagingException | UnsupportedEncodingException e) {
            throw new ScraperException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendEmail(String to, String subject, String body) {
        sendEmail(to, subject, body, null, null);
    }

    public void sendNewSubscriptionEmail(UserSubscription subscription) {
        try {
            String subject = "Welcome to INNOVILYSE";
            String body = getNewSubscriptionEmailBody(subscription);
            sendEmail(subscription.getAdminUser().getEmail(), subject, body);
        } catch (EmailServiceException e) {
            log.error("Error sending email: " + e.getMessage());
        }
    }

    public void sendStopSubscriptionEmail(UserSubscription subscription) {
        try {
            String subject = "Your INNOVILYSE Subscription has been stopped";
            String body = getStopSubscriptionEmailBody(subscription);
            sendEmail(subscription.getAdminUser().getEmail(), subject, body);
        } catch (EmailServiceException e) {
            log.error("Error sending email: " + e.getMessage());
        }
    }

    private String getStopSubscriptionEmailBody(UserSubscription subscription) throws EmailServiceException {
        try {
            String displayName = subscription.getType().getDisplayName();
            UserData adminUser = subscription.getAdminUser();
            String userNames;
            if (adminUser == null) {
                userNames = "User";
            } else {
                userNames = (adminUser.getFirstName() + " " + adminUser.getLastName()).trim();
            }

            return "<!DOCTYPE html><html>" +
                    "<head><style></style></head>" +
                    "<body>" +
                    "<h1 style=\"text-align:center;background-color:" + PRIMARY_COLOR + ";color:white;padding: 8px;\">" +
                    "<a href=\""+ uiUrl +"\" style=\"color:white;text-decoration:none;\">INNOVILYSE Subscription Stopped</a>" +
                    "</h1>" +
                    "<p style=\"text-align:center\">" +
                    "Dear " +
                    userNames +
                    ",</p>" +
                    "<p style=\"text-align:center\">" +
                    "Your subscription to the <b>" +
                    displayName +
                    " plan</b> has been stopped." +
                    "</p>" +
                    "<p style=\"text-align:center\">" +
                    "You will no longer have access to the features of this plan." +
                    "</p>" +
                    "</body></html>";
        } catch (Exception e) {
            throw new EmailServiceException(e.getMessage());
        }
    }

    private String getNewSubscriptionEmailBody(UserSubscription subscription) throws EmailServiceException {
        try {
            String displayName = subscription.getType().getDisplayName();
            UserData adminUser = subscription.getAdminUser();
            String userNames;
            if (adminUser == null) {
                userNames = "User";
            } else {
                userNames = (adminUser.getFirstName() + " " + adminUser.getLastName()).trim();
            }

            return "<!DOCTYPE html><html>" +
                    "<head><style></style></head>" +
                    "<body>" +
                    "<h1 style=\"text-align:center;background-color:" + PRIMARY_COLOR + ";color:white;padding: 8px;\">" +
                    "<a href=\"" + uiUrl + "\" style=\"color:white;text-decoration:none;\">Welcome to INNOVILYSE</a>" +
                    "</h1>" +
                    "<p style=\"text-align:center\">" +
                    "Dear " +
                    userNames +
                    ",</p>" +
                    "<p style=\"text-align:center\">" +
                    "You have successfully subscribed to the <b>" +
                    displayName +
                    " plan</b>." +
                    "</p>" +
                    "<p style=\"text-align:center\">" +
                    "Your subscription will renew active on <b>" +
                    DateMapper.formatToDisplay(DateMapper.map(subscription.getEndDate())) +
                    " (UTC)</b>.</p>" +
                    "</body></html>";
        } catch (Exception e) {
            throw new EmailServiceException(e.getMessage());
        }
    }

    public void sendNewCallsNotification(SavedSearch savedSearch, List<CallDTO> callDTOS) {
        if (callDTOS.isEmpty()) {
            return;
        }
        try {
            sendEmail(savedSearch.getUserData().getEmail(),
                    callDTOS.size() + " New Calls Available on INNOVILYSE for \"" + savedSearch.getName() + "\"", getCallNotificationEmailBody(callDTOS.size() + " New Calls Available on INNOVILYSE for \"" + savedSearch.getName() + "\"",
                            callDTOS
                    ));
        } catch (EmailServiceException e) {
            log.error("Error sending email: " + e.getMessage());
        }
    }

    private String getCallNotificationEmailBody(String subject, List<CallDTO> callDTOS) throws EmailServiceException {
        // as html
        String noWrapCell = "<td style=\"white-space: nowrap\">";

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>");
        builder.append("<head><style>table {width: 100%; border: 1px solid lightgrey; padding: 4px; border-radius: 8px; border-collapse: collapse;} ");
        builder.append("th, td {border: 1px solid lightgrey; padding: 4px; text-align: left;}</style></head>");
        builder.append("<body>");
        builder.append("<h1 style=\"text-align:center;background-color:" + PRIMARY_COLOR + ";color:white;padding: 8px;\">");
        builder.append("<a href=\"").append(uiUrl).append("\" style=\"color:white;text-decoration:none;\">INNOVILYSE</a>");
        builder.append("</h1>");
        builder.append("<h2 style=\"text-align:center\">")
        .append(subject)
        .append("</h2>");
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
//        String seeMoreUrl = uiUrl + "/search?query=" + savedSearch.getValue() + "&statusFilters=UPCOMING,OPEN";
        builder.append("<h4 style=\"text-align:center\">");
//        builder.append("<a href=\"").append(seeMoreUrl).append("\">Search more</a></h4>");
        builder.append("</body></html>");

        return builder.toString();
    }

    public void sendInvoice(String email, Payment savedPayment) {
        try {

            ByteArrayInputStream invoicePdf = invoiceService.generatePdf(savedPayment);

            String fileName = "invoice";

            sendEmail(email,
                    "INNOVILYSE Invoice - " + savedPayment.getCreatedAtDisplayDate(),
                    getInvoiceEmailBody(savedPayment),
                    fileName + ".pdf",
                    invoicePdf);
        } catch (EmailServiceException e) {
            log.error("Error sending email: " + e.getMessage());
        } catch (InvoiceException e) {
            log.error("Error generating invoice: " + e.getMessage());
        }
    }

    private String getInvoiceEmailBody(Payment payment) throws EmailServiceException {
        try {
            String createdAt = payment.getCreatedAtDisplay();
            if (createdAt == null) {
                createdAt = "N/A";
            }

            UserData user = payment.getUserData();
            String userNames;
            if (user == null) {
                userNames = "User";
            } else {
                userNames = (user.getFirstName() + " " + user.getLastName()).trim();
            }
            String currency = payment.getCurrencyDisplay();

            BigDecimal amount = payment.getAmount();
            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html><html>");
            builder.append("<head><style>table {width: 100%; border: 1px solid lightgrey; padding: 4px; border-radius: 8px; border-collapse: collapse;} ");
            builder.append("th, td {border: 1px solid lightgrey; padding: 4px; text-align: left;}</style></head>");
            builder.append("<body>");
            builder.append("<h1 style=\"text-align:center;background-color:" + PRIMARY_COLOR + ";color:white;padding: 8px;\">");
            builder.append("<a href=\"").append(uiUrl).append("\" style=\"color:white;text-decoration:none;\">INNOVILYSE Invoice").append("</a>");
            builder.append("</h1>");

            builder.append("<p>Dear ").append(userNames).append(",</p>");

            builder.append("<p>Your payment of <b>").append(amount).append(" ")
                    .append(currency)
                    .append("</b> has been received at <b>").append(createdAt).append(" (UTC)</b>.</p>");
            builder.append("<p>Please find your <b>invoice</b> is attached to this email.</p>");


            builder.append("</body></html>");
            return builder.toString();
        } catch (Exception e) {
            throw new EmailServiceException(e.getMessage());
        }
    }
}
