package com.jeniustech.funding_search_engine.services;

import com.google.gson.JsonSyntaxException;
import com.jeniustech.funding_search_engine.dto.PaymentSessionDTO;
import com.jeniustech.funding_search_engine.entities.UserData;
import com.jeniustech.funding_search_engine.entities.UserSubscription;
import com.jeniustech.funding_search_engine.enums.SubscriptionStatusEnum;
import com.jeniustech.funding_search_engine.enums.SubscriptionTypeEnum;
import com.jeniustech.funding_search_engine.exceptions.*;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.repository.SubscriptionRepository;
import com.jeniustech.funding_search_engine.repository.UserDataRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.stripe.net.ApiResource;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeService {

    private final String endpointSecret;
    private final UserDataRepository userDataRepository;
    private final SubscriptionRepository subscriptionRepository;

    public StripeService(
            @Value("${stripe.api.key}") String apiKey,
            @Value("${stripe.webhook.secret}") String endpointSecret,
            UserDataRepository userDataRepository,
            SubscriptionRepository subscriptionRepository
    ) {
        Stripe.apiKey = apiKey;
        this.endpointSecret = endpointSecret;
        this.userDataRepository = userDataRepository;
        this.subscriptionRepository = subscriptionRepository;
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

            return sessionDTO;
        } catch (StripeException e) {
            System.out.println("Error creating checkout session: " + e.getMessage());
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
            System.out.println("⚠️  Webhook error while parsing basic request.");
            throw new StripeWebhookException("Invalid payload");
        }
        if (endpointSecret != null && sigHeader != null) {
            try {
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } catch (SignatureVerificationException e) {
                // Invalid signature
                System.out.println("⚠️  Webhook error while validating signature.");
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
                    // TODO cancel existing subscription
                    // TODO refund existing subscription
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
            }
            case  "customer.subscription.resumed" -> {
                Subscription stripeSubscription = (Subscription) stripeObject;
                UserSubscription subscription = subscriptionRepository.findByStripeId(stripeSubscription.getId()).orElseThrow(() -> new NotFoundItemException("Subscription not found"));
                subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
                subscriptionRepository.save(subscription);
            }
            case "customer.subscription.deleted", "customer.subscription.paused" -> {
                Subscription stripeSubscription = (Subscription) stripeObject;
                UserSubscription subscription = subscriptionRepository.findByStripeId(stripeSubscription.getId()).orElseThrow(() -> new NotFoundItemException("Subscription not found"));
                subscription.setStatus(SubscriptionStatusEnum.INACTIVE);
                subscriptionRepository.save(subscription);
            }
            default -> System.out.println("Unhandled event type: " + event.getType());
        }
    }


}
