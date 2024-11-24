package com.jeniustech.funding_search_engine.services.solr;

import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.entities.UserSubscriptionJoin;
import com.jeniustech.funding_search_engine.enums.SubscriptionJoinType;
import com.jeniustech.funding_search_engine.enums.SubscriptionStatusEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.LogBookRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SolrClientTest {

    final JwtModel JWT_MODEL = JwtModel.builder().userId("userId").build();

    @MockBean
    private UserDataRepository userDataRepository;

    @MockBean
    private LogBookRepository logBookRepository;

    @BeforeEach
    public void setUp() {
        UserData userData = UserData.builder()
                .subjectId("userId")
                .userSubscriptionJoins(List.of(
                        UserSubscriptionJoin.builder().
                                type(SubscriptionJoinType.ADMIN)
                                .subscription(
                                        UserSubscription.builder()
                                                .status(SubscriptionStatusEnum.ACTIVE)
                                                .type(SubscriptionTypeEnum.ENTERPRISE)
                                                .endDate(DateMapper.mapToTimestamp("9999-01-01T01:01:01"))
                                                .build()
                                ).build()))
                .build();
        when(userDataRepository.findBySubjectId(JWT_MODEL.getUserId())).thenReturn(Optional.of(userData));
        when(logBookRepository.save(any())).thenReturn(null);
    }
}
