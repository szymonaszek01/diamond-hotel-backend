package com.app.diamondhotelbackend.service.email;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.EmailUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    private final EmailUtil emailUtil;

    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public void sendConfirmationAccountEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.EMAIL_CONFIRM_ACCOUNT_CALLBACK_URI)
                .queryParam(ConstantUtil.EMAIL_ATTR_CONFIRMATION_TOKEN, UrlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        Context context = new Context();
        context.setVariable("userProfile", confirmationToken.getUserProfile());
        context.setVariable("link", link);
        String htmlToString = springTemplateEngine.process("confirmationtokenemail", context);

        emailUtil.send(confirmationToken.getUserProfile().getEmail(), htmlToString, ConstantUtil.EMAIL_CONFIRM_ACCOUNT_SUBJECT, null, null);
    }

    @Override
    public void sendConfirmationPasswordChangingEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.EMAIL_CHANGE_PASSWORD_CALLBACK_URI)
                .queryParam(ConstantUtil.EMAIL_ATTR_CONFIRMATION_TOKEN, UrlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        Context context = new Context();
        context.setVariable("userProfile", confirmationToken.getUserProfile());
        context.setVariable("link", link);
        String htmlToString = springTemplateEngine.process("changingpasswordemail", context);

        emailUtil.send(confirmationToken.getUserProfile().getEmail(), htmlToString, ConstantUtil.EMAIL_CHANGE_PASSWORD_SUBJECT, null, null);
    }

    @Override
    public void sendReservationConfirmedEmail(Reservation reservation, InputStreamResource inputStreamResource) {
        Context context = new Context();
        context.setVariable("reservation", reservation);
        String htmlToString = springTemplateEngine.process("reservationconfirmedemail", context);

        emailUtil.send(reservation.getUserProfile().getEmail(), htmlToString, ConstantUtil.EMAIL_RESERVATION_CONFIRMED_SUBJECT, inputStreamResource, "reservation.pdf");
    }

    @Override
    public void sendPaymentForReservationConfirmedEmail(Payment payment, InputStreamResource inputStreamResource) {
        Context context = new Context();
        context.setVariable("payment", payment);
        String htmlToString = springTemplateEngine.process("paymentforreservationconfirmedemail", context);

        emailUtil.send(payment.getReservation().getUserProfile().getEmail(), htmlToString, ConstantUtil.EMAIL_PAYMENT_FOR_RESERVATION_CONFIRMED_SUBJECT, inputStreamResource, "payment.pdf");
    }

    @Override
    public void sendPaymentForReservationCancelledEmail(Payment payment, InputStreamResource inputStreamResource) {
        Context context = new Context();
        context.setVariable("payment", payment);
        String htmlToString = springTemplateEngine.process("paymentforreservationcancelledemail", context);

        emailUtil.send(payment.getReservation().getUserProfile().getEmail(), htmlToString, ConstantUtil.EMAIL_PAYMENT_FOR_RESERVATION_CANCELLED_SUBJECT, inputStreamResource, "payment.pdf");
    }
}
