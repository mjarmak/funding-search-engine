package com.jeniustech.funding_search_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class UserDataDTO {
    Long id;
    String subjectId;
    String userName;
    String email;
    String password;
    String firstName;
    String lastName;
    SubscriptionDTO subscription;
    Boolean isAdmin;
}
