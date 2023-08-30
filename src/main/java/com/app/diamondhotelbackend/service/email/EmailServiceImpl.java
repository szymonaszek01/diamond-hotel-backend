package com.app.diamondhotelbackend.service.email;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.EmailUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    private final EmailUtil emailUtil;

    @Override
    public void sendConfirmationAccountEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.EMAIL_CONFIRM_ACCOUNT_CALLBACK_URI)
                .queryParam(ConstantUtil.EMAIL_ATTR_CONFIRMATION_TOKEN, UrlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        emailUtil.send(
                confirmationToken.getUserProfile().getEmail(),
                ConstantUtil.buildEmail(confirmationToken.getUserProfile().getFirstname(),
                        ConstantUtil.EMAIL_CONFIRM_ACCOUNT_CONTENT_TITLE,
                        ConstantUtil.EMAIL_CONFIRM_ACCOUNT_CONTENT_DESCRIPTION,
                        ConstantUtil.EMAIL_CONFIRM_ACCOUNT_LINK_DESCRIPTION,
                        link
                ),
                ConstantUtil.EMAIL_CONFIRM_ACCOUNT_SUBJECT
        );
    }

    @Override
    public void sendConfirmationPasswordChangingEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.EMAIL_CHANGE_PASSWORD_CALLBACK_URI)
                .queryParam(ConstantUtil.EMAIL_ATTR_CONFIRMATION_TOKEN, UrlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        emailUtil.send(
                confirmationToken.getUserProfile().getEmail(),
                ConstantUtil.buildEmail(confirmationToken.getUserProfile().getFirstname(),
                        ConstantUtil.EMAIL_CHANGE_PASSWORD_CONTENT_TITLE,
                        ConstantUtil.EMAIL_CHANGE_PASSWORD_CONTENT_DESCRIPTION,
                        ConstantUtil.EMAIL_CHANGE_PASSWORD_LINK_DESCRIPTION,
                        link
                ),
                ConstantUtil.EMAIL_CHANGE_PASSWORD_SUBJECT
        );
    }
}
