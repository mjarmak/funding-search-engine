package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.UserDataDTO;
import com.jeniustech.funding_search_engine.entities.LogBook;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.entities.UserSubscriptionJoin;
import com.jeniustech.funding_search_engine.enums.*;
import com.jeniustech.funding_search_engine.exceptions.MapperException;
import com.jeniustech.funding_search_engine.exceptions.NotFoundItemException;
import com.jeniustech.funding_search_engine.exceptions.TrialSubscriptionException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.SubscriptionRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.jeniustech.funding_search_engine.repository.UserSubscriptionJoinRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserDataService {

    private final Keycloak keycloak;
    private final UserDataRepository userDataRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionJoinRepository userSubscriptionJoinRepository;
    private final LogService logService;

    @Value("${keycloak.service.realm}")
    private String serviceRealm;

    @Transactional
    public List<UserDataDTO> getUsersBySubscriptionId(Long subscriptionId, JwtModel jwtModel) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        UserData adminUserData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserIsAdmin(adminUserData, subscription);

        return subscription.getUserSubscriptionJoins().stream()
                .filter(userSubscriptionJoin -> userSubscriptionJoin.getType() == SubscriptionJoinType.USER)
                .map(userSubscriptionJoin -> UserDataMapper.mapToDTO(userSubscriptionJoin.getUserData(), false))
                .toList();
    }

    @Transactional
    public UserDataDTO addUser(Long subscriptionId, JwtModel jwtModel, UserDataDTO userDataDTO) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        validateAddUserByAdmin(jwtModel, subscription);

        Optional<UserData> userDataOptional = userDataRepository.findByUserName(userDataDTO.getUsername());

        if (userDataOptional.isPresent()) {
            return addExistingUserToSubscription(subscription, userDataOptional.get());
        } else {
            return createUserAndAddToSubscription(subscription, userDataDTO);
        }
    }

    private void validateAddUserByAdmin(JwtModel jwtModel, UserSubscription subscription) {
        if (!subscription.isActive()) {
            throw new ForbiddenException("Subscription is not active");
        }

        validateSubscriptionIsNotTrial(subscription, "Cannot add user to trial subscription plan");

        validateSubscriptionIsNotIndividual(subscription, "Cannot add user to individual subscription plan");

        UserData adminUserData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserIsAdmin(adminUserData, subscription);

        if (subscription.getType().getParent().equals(SubscriptionTypeParentEnum.EU_OFFICE) && subscription.getUserSubscriptionJoins().size() >= 5) {
            throw new ForbiddenException("Cannot have more than 5 users in total, please upgrade your subscription plan");
        }
    }

    private UserDataDTO addExistingUserToSubscription(UserSubscription subscription, UserData userData) {

        UserSubscription mainSubscription = userData.getMainSubscription();
        if (mainSubscription.equals(subscription)) {
            throw new ForbiddenException("User already part of organization's subscription plan");
        } else if (!mainSubscription.isTrial() && mainSubscription.isActive()) {
            throw new ForbiddenException("User already has a subscription");
        }

        UserSubscriptionJoin userSubscriptionJoin = UserSubscriptionJoin.builder()
                .userData(userData)
                .type(SubscriptionJoinType.USER)
                .subscription(subscription)
                .build();

        userSubscriptionJoinRepository.save(userSubscriptionJoin);

        subscription.getUserSubscriptionJoins().add(userSubscriptionJoin);
        subscriptionRepository.save(subscription);

        return UserDataMapper.mapToDTO(userData, false);
    }

    private UserDataDTO createUserAndAddToSubscription(UserSubscription subscription, UserDataDTO userDataDTO) {

        validateUserToCreate(userDataDTO);

        UserRepresentation userRepresentation = UserDataMapper.map(userDataDTO);

        try (Response response = keycloak.realm(serviceRealm).users().create(userRepresentation)) {
            if (response.getStatus() == 201 || response.getStatus() == 409) {

                UserRepresentation newUser = keycloak.realm(serviceRealm).users().search(userDataDTO.getUsername())
                        .stream().findFirst().orElseThrow(() -> new UserNotFoundException("User not found"));

                UserData userData = UserDataMapper.map(newUser);

                UserSubscriptionJoin userSubscriptionJoin = UserSubscriptionJoin.builder()
                        .userData(userData)
                        .type(SubscriptionJoinType.USER)
                        .subscription(subscription)
                        .build();

                subscription.getUserSubscriptionJoins().add(userSubscriptionJoin);

                userDataRepository.save(userData);
                userSubscriptionJoinRepository.save(userSubscriptionJoin);

                addTrialSubscription(userData);

                return UserDataMapper.mapToDTO(userData.getId(), userRepresentation);

            } else {
                throw new InternalError("Error creating user, " + response.getStatusInfo().getReasonPhrase());
            }
        } catch (Exception e) {
            throw new InternalError("Error creating user, " + e.getMessage());
        }
    }

    private void addTrialSubscription(UserData userData) {
        LocalDateTime endDate = getEndDate();
        UserSubscription subscription = UserSubscription.builder()
                .type(SubscriptionTypeEnum.TRIAL)
                .status(SubscriptionStatusEnum.ACTIVE)
                .trialEndDate(DateMapper.map(endDate))
                .build();

        UserSubscriptionJoin userSubscriptionJoin = UserSubscriptionJoin.builder()
                .type(SubscriptionJoinType.ADMIN)
                .subscription(subscription)
                .build();
        subscription.setUserSubscriptionJoins(List.of(userSubscriptionJoin));

        userData.getUserSubscriptionJoins().add(userSubscriptionJoin);

        userSubscriptionJoin.setUserData(userData);
        subscription.setAdminUser(userData);

        subscriptionRepository.save(subscription);
        userSubscriptionJoinRepository.save(userSubscriptionJoin);
    }

    private void validateUserToCreate(UserDataDTO userDataDTO) {
        if (
                userDataDTO.getEmail() == null || userDataDTO.getEmail().isBlank() ||
                        userDataDTO.getFirstName() == null || userDataDTO.getFirstName().isBlank() ||
                        userDataDTO.getLastName() == null || userDataDTO.getLastName().isBlank() ||
                        userDataDTO.getUsername() == null || userDataDTO.getUsername().isBlank() ||
                        userDataDTO.getPassword() == null || userDataDTO.getPassword().isBlank()
        ) {
            throw new MapperException("User data is incomplete");
        }
    }

    @Transactional
    public UserDataDTO getUserDataOrCreate(JwtModel jwtDTO) {
        Optional<UserData> userSettingsOptional = userDataRepository.findBySubjectId(jwtDTO.getUserId());
        if (userSettingsOptional.isPresent()) {
            UserData userData = userSettingsOptional.get();
            boolean updated = false;
            if (userData.getEmail() == null || !userData.getEmail().equals(jwtDTO.getEmail())) {
                userData.setEmail(jwtDTO.getEmail());
                updated = true;
            }
            if (userData.getFirstName() == null || !userData.getFirstName().equals(jwtDTO.getFirstName())) {
                userData.setFirstName(jwtDTO.getFirstName());
                updated = true;
            }
            if (userData.getLastName() == null || !userData.getLastName().equals(jwtDTO.getLastName())) {
                userData.setLastName(jwtDTO.getLastName());
                updated = true;
            }
            if (userData.getUserName() == null || !userData.getUserName().equals(jwtDTO.getUserName())) {
                userData.setUserName(jwtDTO.getUserName());
                updated = true;
            }
            if (updated) {
                userDataRepository.save(userData);
            }
            return UserDataMapper.mapToDTO(userData, true);
        } else {
            UserData userData = UserDataMapper.map(jwtDTO);

            userDataRepository.save(userData);

            addTrialSubscription(userData);

            return UserDataMapper.mapToDTO(userData, true);
        }
    }

    private static LocalDateTime getEndDate() {
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1).plusDays(1);
        endDate = endDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        return endDate;
    }

    @Transactional
    public void removeUserFromSubscription(Long subscriptionId, Long userId, JwtModel jwtModel) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        UserData adminUserData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        ValidatorService.validateUserIsAdmin(adminUserData, subscription);

        UserSubscriptionJoin userSubscriptionJoin = subscription.getUserSubscriptionJoins().stream()
                .filter(join -> join.getUserData().getId().equals(userId))
                .findFirst().orElseThrow(() -> new UserNotFoundException("User not found in subscription"));

        subscription.getUserSubscriptionJoins().remove(userSubscriptionJoin);
        subscriptionRepository.save(subscription);
        userSubscriptionJoinRepository.delete(userSubscriptionJoin);
    }

    private void validateSubscriptionIsNotTrial(UserSubscription subscription, String message) {
        if (subscription.isTrial()) {
            throw new TrialSubscriptionException(message);
        }
    }

    private void validateSubscriptionIsNotIndividual(UserSubscription subscription, String message) {
        if (subscription.isIndividual()) {
            throw new TrialSubscriptionException(message);
        }
    }

    @Transactional(readOnly = true)
    public UserDataDTO getUserDataByUsername(String username) {
        UserData userData = userDataRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserDataMapper.mapToDisplayDTO(userData);
    }

    @Transactional(readOnly = true)
    public List<String> getSearchHistory(String userId, LogTypeEnum type) {
        UserData userData = userDataRepository.findBySubjectId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return logService.getLogsByUserIdAndType(userData.getId(), type, 20).stream().sorted(
                (o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())
        ).map(LogBook::getLogText).toList();
    }
}
