package com.app.diamondhotelbackend.service.email;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.UserProfile;
import org.springframework.core.io.InputStreamResource;

public interface EmailService {

    void sendConfirmationAccountEmail(ConfirmationToken confirmationToken);

    void sendConfirmationPasswordChangingEmail(ConfirmationToken confirmationToken);

    void sendReservationConfirmedEmail(Reservation reservation, InputStreamResource inputStreamResource);

    void sendPaymentForReservationConfirmedEmail(Payment payment, UserProfile userProfile, long reservationId, InputStreamResource inputStreamResource);

    void sendPaymentForReservationCancelledEmail(Payment payment, UserProfile userProfile, long reservationId, InputStreamResource inputStreamResource);
}
