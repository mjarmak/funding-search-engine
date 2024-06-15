package com.jeniustech.funding_search_engine.services;

import com.jeniustech.funding_search_engine.dto.UserDataDTO;
import com.jeniustech.funding_search_engine.entities.Payment;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.entities.UserSubscriptionJoin;
import com.jeniustech.funding_search_engine.enums.CurrencyEnum;
import com.jeniustech.funding_search_engine.enums.PaymentStatusEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionJoinType;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.NotFoundItemException;
import com.jeniustech.funding_search_engine.mappers.DateMapper;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.PaymentRepository;
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
import java.math.BigDecimal;
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
    private final PaymentRepository paymentRepository;

    @Value("${keycloak.service.realm}")
    private String serviceRealm;

    private void validateUserIsAdmin(JwtModel jwtModel, UserSubscription subscription) throws ForbiddenException {
        UserData adminUserData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new NotFoundItemException("User not found"));

        if (!subscription.isAdmin(adminUserData)) {
            throw new ForbiddenException("User is not admin of subscription");
        }
    }

    public List<UserDataDTO> getUsersBySubscriptionId(Long subscriptionId, JwtModel jwtModel) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        validateUserIsAdmin(jwtModel, subscription);

        return subscription.getUserSubscriptionJoins().stream()
                .filter(userSubscriptionJoin -> userSubscriptionJoin.getType() == SubscriptionJoinType.USER)
                .map(userSubscriptionJoin -> UserDataMapper.mapToDTO(userSubscriptionJoin.getUserData(), false))
                .toList();
    }

    public UserDataDTO addUserToSubscription(Long subscriptionId, JwtModel jwtModel, String username) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        validateUserIsAdmin(jwtModel, subscription);

        UserRepresentation userRepresentation;
        try {
            userRepresentation = keycloak.realm(serviceRealm).users().search(username, true)
                    .stream().findFirst().orElseThrow(NotFoundItemException::new);

            UserData userData = userDataRepository.findBySubjectId(userRepresentation.getId())
                    .orElseThrow(() -> new NotFoundItemException("User not found"));

            if (!userData.getMainActiveSubscription().getType().equals(SubscriptionTypeEnum.TRIAL)) {
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

            return UserDataMapper.mapToDTO(userData.getId(), userRepresentation);

        } catch (NotFoundItemException e) {
            throw new NotFoundItemException("User not found");
        }
    }

    public UserDataDTO createUserAndSubscription(Long subscriptionId, JwtModel jwtModel, UserDataDTO userDataDTO) {

        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        validateUserIsAdmin(jwtModel, subscription);

        UserRepresentation userRepresentation = UserDataMapper.map(userDataDTO);

        try (Response response = keycloak.realm(serviceRealm).users().create(userRepresentation)) {
            if (response.getStatus() == 201) {

                UserRepresentation newUser = keycloak.realm(serviceRealm).users().search(userDataDTO.getUserName())
                        .stream().findFirst().orElseThrow(NotFoundItemException::new);

                UserData userData = UserDataMapper.map(newUser);

                UserSubscriptionJoin userSubscriptionJoin = UserSubscriptionJoin.builder()
                        .userData(userData)
                        .type(SubscriptionJoinType.USER)
                        .subscription(subscription)
                        .build();

                userSubscriptionJoinRepository.save(userSubscriptionJoin);

                return UserDataMapper.mapToDTO(userData.getId(), userRepresentation);

            } else {
                throw new InternalError("Error creating user, " + response.getStatusInfo().getReasonPhrase());
            }
        } catch (Exception e) {
            throw new InternalError("Error creating user, " + e.getMessage());
        }
    }

    public UserDataDTO getUserDataOrCreate(JwtModel jwtDTO) {
        Optional<UserData> userSettingsOptional = userDataRepository.findBySubjectId(jwtDTO.getUserId());
        if (userSettingsOptional.isPresent()) {
            UserData userSettings = userSettingsOptional.get();
            boolean updated = false;
            if (userSettings.getEmail() == null || !userSettings.getEmail().equals(jwtDTO.getEmail())) {
                userSettings.setEmail(jwtDTO.getEmail());
                updated = true;
            }
            if (userSettings.getFirstName() == null || !userSettings.getFirstName().equals(jwtDTO.getFirstName())) {
                userSettings.setFirstName(jwtDTO.getFirstName());
                updated = true;
            }
            if (userSettings.getLastName() == null || !userSettings.getLastName().equals(jwtDTO.getLastName())) {
                userSettings.setLastName(jwtDTO.getLastName());
                updated = true;
            }
            if (userSettings.getUserName() == null || !userSettings.getUserName().equals(jwtDTO.getUserName())) {
                userSettings.setUserName(jwtDTO.getUserName());
                updated = true;
            }
            if (updated) {
                userDataRepository.save(userSettings);
            }
            return UserDataMapper.mapToDTO(userSettings, true);
        } else {
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusMonths(1);

            Payment payment = Payment.builder()
                    .amount(BigDecimal.ZERO)
                    .currency(CurrencyEnum.EUR)
                    .startDate(DateMapper.map(startDate))
                    .endDate(DateMapper.map(endDate))
                    .status(PaymentStatusEnum.PAID)
                    .build();
            UserSubscription subscription = UserSubscription.builder()
                    .type(SubscriptionTypeEnum.TRIAL)
                    .payments(List.of(payment))
                    .build();
            payment.setSubscription(subscription);

            UserSubscriptionJoin userSubscriptionJoin = UserSubscriptionJoin.builder()
                    .type(SubscriptionJoinType.ADMIN)
                    .subscription(subscription)
                    .build();
            subscription.setUserSubscriptionJoins(List.of(userSubscriptionJoin));

            UserData userData = UserDataMapper.map(jwtDTO);
            userData.setUserSubscriptionJoins(List.of(userSubscriptionJoin));

            userSubscriptionJoin.setUserData(userData);
            subscription.setAdminUser(userData);

            userDataRepository.save(userData);
            subscriptionRepository.save(subscription);
            userSubscriptionJoinRepository.save(userSubscriptionJoin);
            paymentRepository.save(payment);

            return UserDataMapper.mapToDTO(userData, true);
        }
    }

    public void removeUserFromSubscription(Long subscriptionId, Long userId, JwtModel jwtModel) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        validateUserIsAdmin(jwtModel, subscription);

        UserData userData = userDataRepository.findById(userId).orElseThrow(() -> new NotFoundItemException("User not found"));

        UserSubscriptionJoin userSubscriptionJoin = subscription.getUserSubscriptionJoins().stream()
                .filter(join -> join.getUserData().getId().equals(userId))
                .findFirst().orElseThrow(() -> new NotFoundItemException("User not found in subscription"));

        subscription.getUserSubscriptionJoins().remove(userSubscriptionJoin);
        subscriptionRepository.save(subscription);
        userSubscriptionJoinRepository.delete(userSubscriptionJoin);
    }
}
