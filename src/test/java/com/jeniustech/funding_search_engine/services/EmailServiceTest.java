package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.search.CallDTO;
import com.jeniustech.funding_search_engine.entities.SavedSearch;
import com.jeniustech.funding_search_engine.entities.UserData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Disabled
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendNewCallsNotification() {
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
                        .email("mohamadjarmak@gmail.com")
                        .build())
                .build();
        emailService.sendNewCallsNotification(savedSearch, callDTOS);
    }

}
