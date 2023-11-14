package com.app.diamondhotelbackend.service.payment;


import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.dto.table.model.ReservationPaymentReservedRoomTableFilter;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.PaymentRepository;
import com.app.diamondhotelbackend.service.stripe.StripeServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.PdfUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.app.diamondhotelbackend.specification.PaymentSpecification.*;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final StripeServiceImpl stripeService;

    private final PdfUtil pdfUtil;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentList(int page, int size, JSONObject filters, JSONArray sort) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        return preparePaymentList(0, page, size, filters, sort);
    }

    @Override
    public List<Payment> getPaymentListByUserProfileId(long userProfileId, int page, int size, JSONObject filters, JSONArray sort) {
        if (userProfileId < 1 || page < 0 || size < 1) {
            return Collections.emptyList();
        }

        return preparePaymentList(userProfileId, page, size, filters, sort);
    }

    @Override
    public Payment getPaymentById(long id) throws PaymentProcessingException {
        return paymentRepository.findById(id).orElseThrow(() -> new PaymentProcessingException(ConstantUtil.PAYMENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public FileResponseDto getPaymentPdfDocumentById(long id) throws PaymentProcessingException, IOException {
        Payment payment = getPaymentById(id);
        InputStreamResource inputStreamResource = pdfUtil.getPaymentForReservationPdf(payment);
        String encodedFile = inputStreamResource != null ? Base64.getEncoder().encodeToString(inputStreamResource.getContentAsByteArray()) : "";

        return FileResponseDto.builder()
                .fileName("Payment" + id + ".pdf")
                .encodedFile(encodedFile)
                .build();
    }

    @Override
    public Long countPaymentList() {
        return paymentRepository.count();
    }

    @Override
    public Long countPaymentListByUserProfileId(long userProfileId) throws UserProfileProcessingException {
        Specification<Payment> paymentSpecification = Specification.where(userProfileIdEqual(userProfileId));

        return paymentRepository.count(paymentSpecification);
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

    private List<Payment> preparePaymentList(long userProfileId, int page, int size, JSONObject filters, JSONArray sort) {
        ReservationPaymentReservedRoomTableFilter tableFilters = new ReservationPaymentReservedRoomTableFilter(filters);
        Specification<Payment> paymentSpecification = Specification.where(userProfileId == 0 ? null : userProfileIdEqual(userProfileId))
                .and(tableFilters.getMinDate() == null || tableFilters.getMaxDate() == null ? null : reservationCheckInReservationCheckOutBetween(tableFilters.getMinDate(), tableFilters.getMaxDate()))
                .and(tableFilters.getUserProfileEmail().isEmpty() ? null : userProfileEmailEqual(tableFilters.getUserProfileEmail()))
                .and(tableFilters.getFlightNumber().isEmpty() ? null : flightNumberEqual(tableFilters.getFlightNumber()))
                .and(tableFilters.getPaymentStatus().isEmpty() ? null : paymentStatusEqual(tableFilters.getPaymentStatus()))
                .and(tableFilters.getMinPaymentCost() == null || tableFilters.getMaxPaymentCost() == null ? null : paymentCostBetween(tableFilters.getMinPaymentCost(), tableFilters.getMaxPaymentCost()))
                .and(tableFilters.getPaymentCharge().isEmpty() ? null : paymentChargeEqual(tableFilters.getPaymentCharge()))
                .and(tableFilters.getRoomTypeName().isEmpty() ? null : roomTypeNameEqual(tableFilters.getRoomTypeName()));

        List<Sort.Order> orderList = UrlUtil.toOrderListMapper(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));

        return paymentRepository.findAll(paymentSpecification, pageable).getContent();
    }
}
