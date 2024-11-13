package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.entities.BusinessInformation;
import com.jeniustech.funding_search_engine.entities.Payment;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.exceptions.InvoiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;


@SpringBootTest
public class InvoiceServiceTest {
    @Autowired
    private InvoiceService invoiceService;

    public static final String EMAIL = "mohamadjarmak@gmail.com";

    @Test
    void generatePDF() throws InvoiceException {
        BusinessInformation businessInformation = BusinessInformation.builder().email(EMAIL).build();

        UserData userData = UserData.builder()
                .email(EMAIL)
                .businessInformation(businessInformation)
                .build();

        Payment payment = Payment.builder()
                .stripePaymentId("test")
                .amount(BigDecimal.valueOf(10025))
                .currency("EUR")
                .businessInformation(businessInformation)
                .userData(userData)
                .build();

        invoiceService.generatePdf(payment);
    }

}
