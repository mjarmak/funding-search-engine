package com.jeniustech.funding_search_engine.mappers;

import com.jeniustech.funding_search_engine.dto.UserDataDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.util.CredentialsUtil;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface UserDataMapper {

    static JwtModel map(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        Map<String, Object> claims = jwt.getClaims();
        LinkedTreeMap<String, List<String>> realmAccess = (LinkedTreeMap<String, List<String>>) claims.get("realm_access");
        List<String> roles = realmAccess == null ? Collections.emptyList() : (List<String>) ((LinkedTreeMap) realmAccess).get("roles");


        return JwtModel.builder()
                .userId(jwt.getSubject())
                .firstName(jwt.getClaimAsString("name"))
                .userName(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .userRoles(roles)
                .build();
    }

    static UserRepresentation map(UserDataDTO userInfoDTO) {
        if (userInfoDTO == null) {
            return null;
        }
        CredentialRepresentation credential = CredentialsUtil
                .createPasswordCredentials(userInfoDTO.getPassword());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userInfoDTO.getFirstName());
        userRepresentation.setLastName(userInfoDTO.getLastName());
        userRepresentation.setEmail(userInfoDTO.getEmail());
        userRepresentation.setUsername(userInfoDTO.getUsername());
        userRepresentation.setCredentials(Collections.singletonList(credential));
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    static UserDataDTO mapToDTO(Long id, UserRepresentation userRepresentation) {
        if (userRepresentation == null || id == null) {
            return null;
        }
        return UserDataDTO.builder()
                .id(id)
                .subjectId(userRepresentation.getId())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .username(userRepresentation.getUsername())
                .build();
    }

    static UserDataDTO mapToDTO(UserData userData, boolean withSubData) {
        if (userData == null) {
            return null;
        }
        return UserDataDTO.builder()
                .id(userData.getId())
                .subjectId(userData.getSubjectId())
                .email(userData.getEmail())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .username(userData.getUserName())
                .subscription(withSubData ? SubscriptionMapper.map(userData.getMainActiveSubscription()) : null)
                .isAdmin(withSubData ? userData.isAdmin() : null)
                .build();
    }

    static UserDataDTO mapToDisplayDTO(UserData userData) {
        if (userData == null) {
            return null;
        }
        return UserDataDTO.builder()
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .username(userData.getUserName())
                .build();
    }

    static UserData map(UserRepresentation userRepresentation) {
        if (userRepresentation == null) {
            return null;
        }
        return UserData.builder()
                .subjectId(userRepresentation.getId())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .userName(userRepresentation.getUsername())
                .build();
    }

    static UserData map(JwtModel jwtModel) {
        if (jwtModel == null) {
            return null;
        }
        return UserData.builder()
                .subjectId(jwtModel.getUserId())
                .email(jwtModel.getEmail())
                .firstName(jwtModel.getFirstName())
                .lastName(jwtModel.getLastName())
                .userName(jwtModel.getUserName())
                .build();
    }

}
