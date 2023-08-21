package com.app.diamondhotelbackend.service.email;

import com.app.diamondhotelbackend.entity.ConfirmationToken;

public interface EmailService {

    void sendConfirmationAccountEmail(ConfirmationToken confirmationToken);

    void sendChangingPasswordEmail(ConfirmationToken confirmationToken);
}
