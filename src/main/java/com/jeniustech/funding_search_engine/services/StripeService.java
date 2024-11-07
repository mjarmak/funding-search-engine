package com.jeniustech.funding_search_engine.services;

import com.google.gson.JsonSyntaxException;
import com.jeniustech.funding_search_engine.dto.PaymentSessionDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.enums.SubscriptionStatusEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.NotFoundItemException;
import com.jeniustech.funding_search_engine.exceptions.StripeRequestException;
import com.jeniustech.funding_search_engine.exceptions.StripeWebhookException;
import com.jeniustech.funding_search_engine.exceptions.UserNotFoundException;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.SubscriptionRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class StripeService {

    private final String endpointSecret;
    private final UserDataRepository userDataRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    public StripeService(
            @Value("${stripe.api.key}") String apiKey,
            @Value("${stripe.webhook.secret}") String endpointSecret,
            UserDataRepository userDataRepository,
            SubscriptionRepository subscriptionRepository,
            EmailService emailService) {
        Stripe.apiKey = apiKey;
        this.endpointSecret = endpointSecret;
        this.userDataRepository = userDataRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
    }

    @Transactional
    public PaymentSessionDTO createStripeSession(Long subscriptionId, SubscriptionTypeEnum subscriptionType, String successUrl, String cancelUrl, JwtModel jwtModel) throws StripeRequestException {

        UserSubscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

        UserData userData = userDataRepository.findBySubjectId(jwtModel.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ValidatorService.validateUserIsAdmin(userData, subscription);

        ValidatorService.validateSubscriptionType(userData, subscriptionType);

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(subscriptionType.getPriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .setAutomaticTax(
                        SessionCreateParams.AutomaticTax.builder()
                                .setEnabled(true)
                                .build())
                .setCustomerEmail(userData.getEmail())
                .build();
        try {
            Session session = Session.create(params);
            PaymentSessionDTO sessionDTO = PaymentSessionDTO.builder()
                    .url(session.getUrl())
                    .successUrl(successUrl)
                    .cancelUrl(cancelUrl)
                    .build();


            subscription.setCheckoutSessionId(session.getId());
            subscription.setNextType(subscriptionType);
            subscriptionRepository.save(subscription);

            log.info("Checkout session created: " + session.getId());
            return sessionDTO;
        } catch (StripeException e) {
            log.error("Error creating checkout session: " + e.getMessage());
            throw new StripeRequestException("Error creating checkout session");
        }
    }

    @Transactional
    public void processWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = ApiResource.GSON.fromJson(payload, Event.class);
        } catch (JsonSyntaxException e) {
            // Invalid payload
            log.error("⚠️  Webhook error while parsing basic request.");
            throw new StripeWebhookException("Invalid payload");
        }
        if (endpointSecret != null && sigHeader != null) {
            try {
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } catch (SignatureVerificationException e) {
                // Invalid signature
                log.error("⚠️  Webhook error while validating signature.");
                e.printStackTrace();
                throw new StripeWebhookException("Invalid signature");
            }
        }
        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            throw new StripeWebhookException("Deserialization failed");
        }
        // Handle the event
        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) stripeObject;
                UserSubscription subscription = subscriptionRepository.findByCheckoutSessionId(session.getId()).orElseThrow(() -> new NotFoundItemException("Subscription not found"));

                if (subscription.getStripeId() != null) {
                    try {
                        Subscription stripeSubscription = Subscription.retrieve(subscription.getStripeId());
                        stripeSubscription.cancel(SubscriptionCancelParams.builder().setInvoiceNow(true).build());
                    } catch (StripeException e) {
                        log.error("Error retrieving subscription: " + e.getMessage());
                    }
                }

                subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
                subscription.setCheckoutSessionId(null);
                subscription.setStripeId(session.getSubscription());
                subscription.setEndDateFromNow(subscription.getType().getPeriod());
                subscription.setType(subscription.getNextType());
                subscription.setEndDateFromNow(subscription.getNextType().getPeriod());
                subscription.setNextType(null);
                subscription.setTrialEndDate(null);
                subscriptionRepository.save(subscription);
                emailService.sendNewSubscriptionEmail(subscription);
            }
            case  "customer.subscription.resumed" -> {
                Subscription stripeSubscription = (Subscription) stripeObject;
                Optional<UserSubscription> subscription = subscriptionRepository.findByStripeId(stripeSubscription.getId());
                if (subscription.isEmpty()) {
                    log.warn("Subscription not found: " + stripeSubscription.getId());
                    return;
                }
                subscription.get().setStatus(SubscriptionStatusEnum.ACTIVE);
                subscriptionRepository.save(subscription.get());
            }
            case "customer.subscription.deleted", "customer.subscription.paused" -> {
                Subscription stripeSubscription = (Subscription) stripeObject;
                Optional<UserSubscription> subscriptionOptional = subscriptionRepository.findByStripeId(stripeSubscription.getId());
                if (subscriptionOptional.isEmpty()) {
                    log.warn("Subscription not found: " + stripeSubscription.getId());
                    return;
                }
                final UserSubscription subscription = subscriptionOptional.get();
                subscription.setStatus(SubscriptionStatusEnum.INACTIVE);
                subscriptionRepository.save(subscription);
                emailService.sendStopSubscriptionEmail(subscription);
            }
            default -> log.warn("Unhandled event type: " + event.getType());
        }
    }


}
