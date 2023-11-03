package com.app.diamondhotelbackend.service.payment;

import com.app.diamondhotelbackend.dto.common.PdfResponseDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.stripe.exception.StripeException;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.io.IOException;
import java.util.List;

public interface PaymentService {

    Payment createPayment(Payment payment);

    List<Payment> getPaymentList(int page, int size, String status, JSONArray jsonArray);

    List<Payment> getPaymentListByUserProfileId(long userProfileId, int page, int size, String status, JSONArray jsonArray);

    Payment getPaymentById(long id) throws PaymentProcessingException;

    PdfResponseDto getPaymentPdfDocumentById(long id) throws PaymentProcessingException, IOException;

    Long countPaymentListByUserProfileId(long userProfileId) throws UserProfileProcessingException;

    Payment updatePayment(Payment payment) throws PaymentProcessingException, StripeException;

    Payment deletePayment(Payment payment) throws PaymentProcessingException, StripeException;
}
