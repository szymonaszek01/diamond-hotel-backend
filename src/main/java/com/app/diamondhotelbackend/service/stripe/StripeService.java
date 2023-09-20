package com.app.diamondhotelbackend.service.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;

import java.io.IOException;

public interface StripeService {

    Charge createCharge(String token, int amount) throws StripeException;

    Refund createRefund(String charge) throws StripeException;
}
