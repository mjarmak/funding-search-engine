package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.entities.*;
import com.jeniustech.funding_search_engine.enums.CountryEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Disabled
public class EmailServiceTest {

    public static final String EMAIL = "mohamadjarmak@gmail.com";
    @Autowired
    private EmailService emailService;

    @Test
    void testSendNewSubscriptionEmail() {
        UserSubscription subscription = UserSubscription.builder()
                .adminUser(UserData.builder()
                        .email(EMAIL)
                        .firstName("Mohamad")
                        .lastName("Jarmak")
                        .userName("mohamadjarmak")
                        .build())
                .type(SubscriptionTypeEnum.INDIVIDUAL_YEAR)
                .endDate(Timestamp.valueOf(LocalDateTime.now().plusYears(1)))
                .build();
        emailService.sendNewSubscriptionEmail(subscription);
    }

    @Test
    void testStopSubscriptionEmail() {
        UserSubscription subscription = UserSubscription.builder()
                .adminUser(UserData.builder()
                        .email(EMAIL)
                        .firstName("Mohamad")
                        .lastName("Jarmak")
                        .userName("mohamadjarmak")
                        .build())
                .type(SubscriptionTypeEnum.INDIVIDUAL_YEAR)
                .endDate(Timestamp.valueOf(LocalDateTime.now().plusYears(1)))
                .build();
        emailService.sendStopSubscriptionEmail(subscription);
    }

    @Test
    void testSendNewCallsNotification() {
        List<CallDTO> callDTOS = List.of(
                CallDTO.builder()
                        .id(1L)
                        .identifier("GV-2-2014")
                        .title("Optimised and systematic energy management in electric vehicles")
                        .actionType("RIA Research and Innovation action")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now())
                        .endDate2(LocalDateTime.now())
                        .budgetMin("1000000")
                        .budgetMax("2000000")
                        .build(),
                CallDTO.builder()
                        .id(2L)
                        .identifier("GV-2-2015")
                        .title("Optimised and systematic energy management in electric vehicles")
                        .actionType("RIA Research and Innovation action")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now())
                        .endDate2(LocalDateTime.now())
                        .budgetMin("1000000")
                        .budgetMax(null)
                        .build(),
                CallDTO.builder()
                        .id(3L)
                        .identifier("GV-2-2015")
                        .title("Optimised and systematic energy management in electric vehicles")
                        .actionType("RIA Research and Innovation action")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now())
                        .endDate2(LocalDateTime.now())
                        .budgetMin(null)
                        .budgetMax("2000000")
                        .build(),
                CallDTO.builder()
                        .id(4L)
                        .identifier("GV-2-2015")
                        .title("Optimised and systematic energy management in electric vehicles")
                        .actionType("RIA Research and Innovation action")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now())
                        .endDate2(LocalDateTime.now())
                        .budgetMin(null)
                        .budgetMax(null)
                        .build()
        );
        SavedSearch savedSearch = SavedSearch.builder()
                .name("Electric Vehicles")
                .value("electric vehicles")
                .userData(UserData.builder()
                        .email(EMAIL)
                        .build())
                .build();
        emailService.sendNewCallsNotification(savedSearch, callDTOS);
    }

    @Test
    void sendInvoice_null() {
        Payment payment = Payment.builder().build();
        emailService.sendInvoice(EMAIL, payment);
    }

    @Test
    void sendInvoice_nullUser() {
        Payment payment = Payment.builder()
                .userData(null)
                .amount(BigDecimal.valueOf(50))
                .currency("EUR")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        emailService.sendInvoice(EMAIL, payment);
    }

    @Test
    void sendInvoice_nullBusiness() {
        Payment payment = Payment.builder()
                .userData(UserData.builder()
                        .email(EMAIL)
                        .firstName("Mohamad")
                        .lastName("Jarmak")
                        .build())
                .amount(BigDecimal.valueOf(50))
                .currency("EUR")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .invoiceId("314.2024.11111")
                .build();

        emailService.sendInvoice(EMAIL, payment);
    }

    @Test
    void sendInvoice_minimumInput() {
        Payment payment = Payment.builder()
                .userData(UserData.builder()
                        .email(EMAIL)
                        .firstName("Mohamad")
                        .lastName("Jarmak")
                        .businessInformation(
                                BusinessInformation.builder()
                                        .name("Afinit")
                                        .vatNumber("BE0789424602")
                                        .build())
                        .build())
                .amount(BigDecimal.valueOf(301.29))
                .currency("EUR")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .invoiceId("314.2024.11111")
                .build();

        emailService.sendInvoice(EMAIL, payment);
    }

    @Test
    void sendInvoice() {
        Payment payment = Payment.builder()
                .userData(UserData.builder()
                        .email(EMAIL)
                        .firstName("Mohamad")
                        .lastName("Jarmak")
                        .businessInformation(
                                BusinessInformation.builder()
                                        .name("Afinit")
                                        .vatNumber("BE0789424602")
                                        .email("test@gmail.com")
                                        .phoneNumber("+123456789")
                                        .address(Address.builder()
                                                .street("Avenue des Volontaires 38")
                                                .postCode("1040")
                                                .city("Brussels")
                                                .country(CountryEnum.BE)
                                                .build())
                                        .build())
                        .build())
                .amount(BigDecimal.valueOf(301.29))
                .currency("EUR")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .invoiceId("314.2024.11111")
                .build();

        emailService.sendInvoice(EMAIL, payment);
    }

}
