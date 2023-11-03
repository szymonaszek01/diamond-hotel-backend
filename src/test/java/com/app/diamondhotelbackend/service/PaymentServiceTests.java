package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.common.PdfResponseDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.repository.PaymentRepository;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.stripe.StripeServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.PdfUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.json.JSONArray;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeServiceImpl stripeService;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private PdfUtil pdfUtil;

    private Charge charge;

    private Refund refund;

    private UserProfile userProfile;

    private InputStreamResource inputStreamResource;

    private Payment payment;

    private List<Payment> paymentList;

    private Page<Payment> paymentPage;

    private PdfResponseDto pdfResponseDto;

    @BeforeEach
    public void init() {
        charge = new Charge();
        charge.setId("chargeId1");
        charge.setStatus(ConstantUtil.SUCCEEDED);

        refund = new Refund();
        refund.setId("chargeId1");
        refund.setObject(ConstantUtil.REFUND);

        userProfile = UserProfile.builder().id(1).email("email1").build();

        inputStreamResource = new InputStreamResource(InputStream.nullInputStream());

        payment = Payment.builder().id(1).createdAt(new Date(System.currentTimeMillis())).expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15)).status(ConstantUtil.WAITING_FOR_PAYMENT).build();

        paymentList = List.of(Payment.builder().createdAt(new Date(System.currentTimeMillis())).expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15)).token("token1").build(), Payment.builder().createdAt(new Date(System.currentTimeMillis())).expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15)).token("token2").build(), Payment.builder().createdAt(new Date(System.currentTimeMillis())).expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15)).token("token3").build());

        byte[] bytes = HexFormat.of().parseHex("e04fd020");

        pdfResponseDto = PdfResponseDto.builder().fileName("Payment1.pdf").encodedFile(Base64.getEncoder().encodeToString(bytes)).build();

        paymentPage = new PageImpl<>(paymentList);
    }

    @Test
    public void PaymentService_CreatePayment_ReturnsPayment() {
        when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(payment);

        Payment savedPayment = paymentService.createPayment(payment);

        Assertions.assertThat(savedPayment).isNotNull();
        Assertions.assertThat(savedPayment.getToken()).isEqualTo(payment.getToken());
    }

    @Test
    public void PaymentService_GetPaymentList_ReturnsPaymentList() {
        when(paymentRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(paymentPage);

        List<Payment> foundPaymentList = paymentService.getPaymentList(1, 3, "", new JSONArray());

        Assertions.assertThat(foundPaymentList).isNotNull();
        Assertions.assertThat(foundPaymentList.size()).isEqualTo(3);
    }

    @Test
    public void PaymentService_GetPaymentListByUserProfileId_ReturnsPaymentList() {
        when(paymentRepository.findAllByReservationUserProfileId(Mockito.any(long.class), Mockito.any(PageRequest.class))).thenReturn(paymentPage);

        List<Payment> foundPaymentList = paymentService.getPaymentListByUserProfileId(1, 1, 3, "", new JSONArray());

        Assertions.assertThat(foundPaymentList).isNotNull();
        Assertions.assertThat(foundPaymentList.size()).isEqualTo(3);
    }

    @Test
    public void PaymentService_GetPaymentById_ReturnsPayment() {
        when(paymentRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(payment));

        Payment foundPayment = paymentService.getPaymentById(payment.getId());

        Assertions.assertThat(foundPayment).isNotNull();
        Assertions.assertThat(foundPayment.getId()).isEqualTo(payment.getId());
    }

    @Test
    public void PaymentService_GetPaymentPdfDocumentById_ReturnsPdfResponseDto() throws IOException {
        when(paymentRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(payment));
        when(pdfUtil.getPaymentForReservationPdf(Mockito.any(Payment.class))).thenReturn(inputStreamResource);

        PdfResponseDto createdPdfResponseDto = paymentService.getPaymentPdfDocumentById(payment.getId());

        Assertions.assertThat(createdPdfResponseDto.getFileName()).isEqualTo(pdfResponseDto.getFileName());
    }

    @Test
    public void PaymentService_CountPaymentListByUserProfileId_ReturnsLong() {
        when(userProfileService.getUserProfileById(Mockito.any(long.class))).thenReturn(userProfile);
        when(paymentRepository.countAllByReservationUserProfile(Mockito.any(UserProfile.class))).thenReturn(3L);

        Long countReservationList = paymentService.countPaymentListByUserProfileId(1L);

        Assertions.assertThat(countReservationList).isNotNull();
        Assertions.assertThat(countReservationList).isEqualTo(3L);
    }

    @Test
    public void PaymentService_UpdatePayment_ReturnsPayment() throws StripeException {
        payment.setCost(BigDecimal.valueOf(1000));
        payment.setToken("token1");

        when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(payment);
        when(stripeService.createCharge(Mockito.any(String.class), Mockito.any(int.class))).thenReturn(charge);

        Payment savedPayment = paymentService.updatePayment(payment);

        Assertions.assertThat(savedPayment).isNotNull();
        Assertions.assertThat(savedPayment.getToken()).isEqualTo("token1");
        Assertions.assertThat(savedPayment.getCharge()).isEqualTo(charge.getId());
        Assertions.assertThat(savedPayment.getStatus()).isEqualTo(ConstantUtil.APPROVED);
    }

    @Test
    public void PaymentService_DeletePayment_ReturnsPayment() throws StripeException {
        payment.setCharge("chargeId1");

        when(stripeService.createRefund(Mockito.any(String.class))).thenReturn(refund);

        Payment savedPayment = paymentService.deletePayment(payment);

        verify(paymentRepository).delete(Mockito.any(Payment.class));

        Assertions.assertThat(savedPayment).isNotNull();
        Assertions.assertThat(savedPayment.getStatus()).isEqualTo(ConstantUtil.CANCELLED);
    }
}
