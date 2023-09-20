package com.app.diamondhotelbackend.service.stripe;

import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    @PostConstruct
    public void init() {
        Stripe.apiKey = applicationPropertiesUtil.getStripeSecretKey();
    }

    @Override
    public Charge createCharge(String token, int amount) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();

        chargeParams.put("amount", amount);
        chargeParams.put("currency", "USD");
        chargeParams.put("description", "Reservation - " + token);
        chargeParams.put("source", token);

        return Charge.create(chargeParams);
    }

    @Override
    public Refund createRefund(String charge) throws StripeException {
        Map<String, Object> refundParams = new HashMap<>();

        refundParams.put("charge", charge);

        return Refund.create(refundParams);
    }
}
