package com.jeniustech.funding_search_engine.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtModel {
    private String userId; // subject ID
    private String userName;
    private String firstName;
    private String lastName;
    private List<String> userRoles;
    private String email;

    public boolean isAdmin() {
        return userRoles != null && (userRoles.contains("admin") || userRoles.contains("ADMIN"));
    }

    public boolean isReadOnlyAdmin() {
        return userRoles != null && (userRoles.contains("admin-readonly") || userRoles.contains("ADMIN-READONLY"));
    }
}
