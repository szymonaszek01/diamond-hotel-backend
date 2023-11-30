package com.app.diamondhotelbackend.service.payment;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.stripe.exception.StripeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public interface PaymentService {

    Payment createPayment(Payment payment);

    List<Payment> getPaymentList(int page, int size, JSONObject filters, JSONArray sort);

    List<Payment> getPaymentList(Date min, Date max);

    List<Payment> getPaymentListByUserProfileId(long userProfileId, int page, int size, JSONObject filters, JSONArray sort);

    Payment getPaymentById(long id) throws PaymentProcessingException;

    FileResponseDto getPaymentPdfDocumentById(long id) throws PaymentProcessingException, IOException;

    Long countPaymentList();

    Long countPaymentListByUserProfileId(long userProfileId) throws UserProfileProcessingException;

    Payment updatePayment(Payment payment) throws PaymentProcessingException, StripeException;

    Payment deletePayment(Payment payment) throws PaymentProcessingException, StripeException;
}
