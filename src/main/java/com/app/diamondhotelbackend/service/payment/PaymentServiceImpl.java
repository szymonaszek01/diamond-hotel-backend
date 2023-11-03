package com.app.diamondhotelbackend.service.payment;


import com.app.diamondhotelbackend.dto.common.PdfResponseDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.PaymentRepository;
import com.app.diamondhotelbackend.service.stripe.StripeServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.PdfUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final StripeServiceImpl stripeService;

    private final UserProfileServiceImpl userProfileService;

    private final PdfUtil pdfUtil;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentList(int page, int size, String status, JSONArray jsonArray) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        List<Sort.Order> orderList = UrlUtil.toOrderListMapper(jsonArray);
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));
        Page<Payment> paymentPage;
        if (status.isEmpty()) {
            paymentPage = paymentRepository.findAll(pageable);
        } else {
            paymentPage = paymentRepository.findAllByStatus(status, pageable);
        }

        return paymentPage.getContent();
    }

    @Override
    public List<Payment> getPaymentListByUserProfileId(long userProfileId, int page, int size, String status, JSONArray jsonArray) {
        try {
            if (userProfileId < 1 || page < 0 || size < 1) {
                return Collections.emptyList();
            }

            List<Sort.Order> orderList = UrlUtil.toOrderListMapper(jsonArray);
            Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));
            Page<Payment> paymentPage;
            if (status.isEmpty()) {
                paymentPage = paymentRepository.findAllByReservationUserProfileId(userProfileId, pageable);
            } else {
                paymentPage = paymentRepository.findAllByStatusAndReservationUserProfileId(status, userProfileId, pageable);
            }

            return paymentPage.getContent();

        } catch (AuthProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Payment getPaymentById(long id) throws PaymentProcessingException {
        return paymentRepository.findById(id).orElseThrow(() -> new PaymentProcessingException(ConstantUtil.PAYMENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public PdfResponseDto getPaymentPdfDocumentById(long id) throws PaymentProcessingException, IOException {
        Payment payment = getPaymentById(id);
        InputStreamResource inputStreamResource = pdfUtil.getPaymentForReservationPdf(payment);
        String encodedFile = inputStreamResource != null ? Base64.getEncoder().encodeToString(inputStreamResource.getContentAsByteArray()) : "";

        return PdfResponseDto.builder()
                .fileName("Payment" + id + ".pdf")
                .encodedFile(encodedFile)
                .build();
    }

    @Override
    public Long countPaymentListByUserProfileId(long userProfileId) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileById(userProfileId);

        return paymentRepository.countAllByReservationUserProfile(userProfile);
    }

    @Override
    public Payment updatePayment(Payment payment) throws PaymentProcessingException, StripeException {
        if (payment.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
            paymentRepository.delete(payment);
            throw new PaymentProcessingException(ConstantUtil.PAYMENT_EXPIRED_EXCEPTION);
        }

        if (payment.getToken() != null && payment.getCost() != null) {
            Charge charge = stripeService.createCharge(payment.getToken(), payment.getCost().intValue() * 100);
            payment.setStatus(ConstantUtil.SUCCEEDED.equals(charge.getStatus()) ? ConstantUtil.APPROVED : ConstantUtil.CANCELLED);
            payment.setToken(payment.getToken());
            payment.setCharge(charge.getId());
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment deletePayment(Payment payment) throws PaymentProcessingException, StripeException {
        if (payment.getCharge() != null) {
            Refund refund = stripeService.createRefund(payment.getCharge());
            if (!ConstantUtil.REFUND.equals(refund.getObject())) {
                throw new PaymentProcessingException(ConstantUtil.CHARGE_NOT_FOUND_EXCEPTION);
            }
        }

        payment.setStatus(ConstantUtil.CANCELLED);
        paymentRepository.delete(payment);

        return payment;
    }
}
