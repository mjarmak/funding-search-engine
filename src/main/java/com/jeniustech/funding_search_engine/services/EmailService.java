package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.CallDTO;
import com.jeniustech.funding_search_engine.entities.Call;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.exceptions.ScraperException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final JavaMailSender mailSender;

    public void sendNewCallsNotification(SavedSearch savedSearch, List<CallDTO> callDTOS) {
        StringBuilder text = new StringBuilder("New calls have been found:\n\n");
        for (CallDTO callDTO : callDTOS) {
            text.append(callDTO.getIdentifier()).append(" - ").append(callDTO.getTitle()).append("\n");
            text.append("Action type: ").append(callDTO.getActionType()).append("\n");
            text.append("Proposal Submission Period: ").append(callDTO.getStartDate()).append(" - ").append(callDTO.getEndDate());
            if (callDTO.getEndDate2() != null) {
                text.append(" - ").append(callDTO.getEndDate2());
            }
            text.append("\n");
            text.append("Budget: ").append(Call.getBudgetRangeString(callDTO.getBudgetMin(), callDTO.getBudgetMax())).append("EUR\n\n");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(from, "INNOVILYSE"));
            helper.setReplyTo(from);
            helper.setTo(savedSearch.getUserData().getEmail());
            helper.setSubject("New calls available on INNOVILYSE for \"" + savedSearch.getValue() + "\"");
            helper.setText(text.toString());
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new ScraperException(e.getMessage());
        }
    }
}
