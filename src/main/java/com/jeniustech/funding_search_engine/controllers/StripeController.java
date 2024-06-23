package com.jeniustech.funding_search_engine.controllers;

import com.jeniustech.funding_search_engine.dto.PaymentSessionDTO;
import com.jeniustech.funding_search_engine.mappers.UserDataMapper;
import com.jeniustech.funding_search_engine.models.JwtModel;
import com.jeniustech.funding_search_engine.services.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/subscription/{subscriptionId}/payment/create-checkout-session")
    public ResponseEntity<PaymentSessionDTO> createCheckoutSession(
            @RequestBody PaymentSessionDTO paymentSessionDto,
            @PathVariable Long subscriptionId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        JwtModel jwtModel = UserDataMapper.map(jwt);
        return ResponseEntity.ok(stripeService.createStripeSession(subscriptionId, paymentSessionDto.getType(), paymentSessionDto.getSuccessUrl(), paymentSessionDto.getCancelUrl(), jwtModel));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        stripeService.processWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

}
