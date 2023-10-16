package com.app.diamondhotelbackend.service.payment;


import com.app.diamondhotelbackend.dto.payment.request.PaymentCancelRequestDto;
import com.app.diamondhotelbackend.dto.payment.request.PaymentChargeRequestDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.PaymentRepository;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.stripe.StripeServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.PdfUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private final EmailServiceImpl emailService;

    private final PdfUtil pdfUtil;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentList(int page, int size) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);

        return paymentPage.getContent();
    }

    @Override
    public List<Payment> getPaymentListByUserProfileId(long userProfileId, int page, int size, String status) {
        try {
            if (userProfileId < 1 || page < 0 || size < 1) {
                return Collections.emptyList();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Payment> paymentPage;
            if (status.isEmpty()) {
                paymentPage = paymentRepository.findAllByReservationUserProfileIdOrderByReservationIdDesc(userProfileId, pageable);
            } else {
                paymentPage = paymentRepository.findAllByStatusAndReservationUserProfileIdOrderByReservationIdDesc(status, userProfileId, pageable);
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
    public Long countPaymentListByUserProfileId(long userProfileId) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileById(userProfileId);

        return paymentRepository.countAllByReservationUserProfile(userProfile);
    }

    @Override
    public Payment chargePayment(PaymentChargeRequestDto paymentChargeRequestDto) throws PaymentProcessingException, StripeException, UserProfileProcessingException {
        Payment payment = getPaymentById(paymentChargeRequestDto.getPaymentId());
        if (payment.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
            payment.setStatus(ConstantUtil.CANCELLED);
            paymentRepository.save(payment);

            throw new PaymentProcessingException(ConstantUtil.PAYMENT_EXPIRED_EXCEPTION);
        }

        Charge charge = stripeService.createCharge(paymentChargeRequestDto.getToken(), paymentChargeRequestDto.getAmount());
        payment.setStatus(ConstantUtil.SUCCEEDED.equals(charge.getStatus()) ? ConstantUtil.APPROVED : ConstantUtil.CANCELLED);
        payment.setToken(paymentChargeRequestDto.getToken());
        payment.setCharge(charge.getId());

        UserProfile userProfile = userProfileService.getUserProfileById(paymentChargeRequestDto.getUserProfileId());
        InputStreamResource inputStreamResource = pdfUtil.getPaymentPdf(payment, userProfile, paymentChargeRequestDto.getReservationId());
        emailService.sendPaymentForReservationConfirmedEmail(payment, userProfile, paymentChargeRequestDto.getReservationId(), inputStreamResource);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment cancelPayment(PaymentCancelRequestDto paymentCancelRequestDto) throws PaymentProcessingException, StripeException, UserProfileProcessingException {
        Payment payment = getPaymentById(paymentCancelRequestDto.getPaymentId());

        if (payment.getCharge() != null) {
            Refund refund = stripeService.createRefund(payment.getCharge());
            if (!ConstantUtil.REFUND.equals(refund.getObject())) {
                throw new PaymentProcessingException(ConstantUtil.CHARGE_NOT_FOUND_EXCEPTION);
            }
        }

        payment.setStatus(ConstantUtil.CANCELLED);

        UserProfile userProfile = userProfileService.getUserProfileById(paymentCancelRequestDto.getUserProfileId());
        InputStreamResource inputStreamResource = pdfUtil.getPaymentPdf(payment, userProfile, paymentCancelRequestDto.getReservationId());
        emailService.sendPaymentForReservationCancelledEmail(payment, userProfile, paymentCancelRequestDto.getReservationId(), inputStreamResource);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePaymentStatus(long id, String status) throws PaymentProcessingException {
        Payment payment = getPaymentById(id);
        payment.setStatus(status);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePaymentCost(long id, BigDecimal cost) throws PaymentProcessingException {
        Payment payment = getPaymentById(id);
        payment.setCost(cost);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePaymentToken(long id, String token) throws PaymentProcessingException {
        Payment payment = getPaymentById(id);
        payment.setToken(token);

        return paymentRepository.save(payment);
    }
}
