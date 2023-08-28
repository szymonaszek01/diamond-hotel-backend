package com.app.diamondhotelbackend.service.email;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.util.BaseUriPropertiesProvider;
import com.app.diamondhotelbackend.util.Constant;
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

    private final BaseUriPropertiesProvider baseUriPropertiesProvider;

    private final EmailUtil emailUtil;

    @Override
    public void sendConfirmationAccountEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(baseUriPropertiesProvider.getClient() + Constant.EMAIL_CONFIRM_ACCOUNT_CALLBACK_URI)
                .queryParam(Constant.EMAIL_ATTR_CONFIRMATION_TOKEN, UrlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        emailUtil.send(
                confirmationToken.getUserProfile().getEmail(),
                Constant.buildEmail(confirmationToken.getUserProfile().getFirstname(),
                        Constant.EMAIL_CONFIRM_ACCOUNT_CONTENT_TITLE,
                        Constant.EMAIL_CONFIRM_ACCOUNT_CONTENT_DESCRIPTION,
                        Constant.EMAIL_CONFIRM_ACCOUNT_LINK_DESCRIPTION,
                        link
                ),
                Constant.EMAIL_CONFIRM_ACCOUNT_SUBJECT
        );
    }

    @Override
    public void sendConfirmationPasswordChangingEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(baseUriPropertiesProvider.getClient() + Constant.EMAIL_CHANGE_PASSWORD_CALLBACK_URI)
                .queryParam(Constant.EMAIL_ATTR_CONFIRMATION_TOKEN, UrlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        emailUtil.send(
                confirmationToken.getUserProfile().getEmail(),
                Constant.buildEmail(confirmationToken.getUserProfile().getFirstname(),
                        Constant.EMAIL_CHANGE_PASSWORD_CONTENT_TITLE,
                        Constant.EMAIL_CHANGE_PASSWORD_CONTENT_DESCRIPTION,
                        Constant.EMAIL_CHANGE_PASSWORD_LINK_DESCRIPTION,
                        link
                ),
                Constant.EMAIL_CHANGE_PASSWORD_SUBJECT
        );
    }
}
