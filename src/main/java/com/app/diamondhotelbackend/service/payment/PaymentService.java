package com.app.diamondhotelbackend.service.payment;

import com.app.diamondhotelbackend.dto.payment.request.PaymentCancelRequestDto;
import com.app.diamondhotelbackend.dto.payment.request.PaymentChargeRequestDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.stripe.exception.StripeException;

import java.io.IOException;
import java.math.BigDecimal;

public interface PaymentService {

    Payment createPayment(Payment payment);

    Payment getPaymentById(long id) throws PaymentProcessingException;

    Payment chargePayment(PaymentChargeRequestDto paymentChargeRequestDto) throws PaymentProcessingException, StripeException, UserProfileProcessingException;

    Payment cancelPayment(PaymentCancelRequestDto paymentCancelRequestDto) throws PaymentProcessingException, IOException, StripeException, UserProfileProcessingException;

    Payment updatePaymentStatus(long id, String status) throws PaymentProcessingException;

    Payment updatePaymentCost(long id, BigDecimal cost) throws PaymentProcessingException;

    Payment updatePaymentToken(long id, String token) throws PaymentProcessingException;
}
