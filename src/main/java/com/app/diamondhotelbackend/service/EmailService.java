package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.util.BaseUriPropertiesProvider;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.EmailUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private final BaseUriPropertiesProvider baseUriPropertiesProvider;

    private final EmailUtil emailUtil;

    private final UrlUtil urlUtil;

    @Async
    public void sendConfirmationAccountEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(baseUriPropertiesProvider.getClient() + Constant.EMAIL_CONFIRM_ACCOUNT_CALLBACK_URI)
                .queryParam(Constant.EMAIL_ATTR_CONFIRMATION_TOKEN, urlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        send(
                confirmationToken.getUserProfile().getEmail(),
                emailUtil.buildEmail(confirmationToken.getUserProfile().getFirstname(),
                        Constant.EMAIL_CONFIRM_ACCOUNT_CONTENT_TITLE,
                        Constant.EMAIL_CONFIRM_ACCOUNT_CONTENT_DESCRIPTION,
                        Constant.EMAIL_CONFIRM_ACCOUNT_LINK_DESCRIPTION,
                        link
                ),
                Constant.EMAIL_CONFIRM_ACCOUNT_SUBJECT
        );
    }

    public void sendChangingPasswordEmail(ConfirmationToken confirmationToken) {
        String link = UriComponentsBuilder.fromUriString(baseUriPropertiesProvider.getClient() + Constant.EMAIL_CHANGE_PASSWORD_CALLBACK_URI)
                .queryParam(Constant.EMAIL_ATTR_CONFIRMATION_TOKEN, urlUtil.encode(confirmationToken.getAccessValue()))
                .build()
                .toUriString();

        send(
                confirmationToken.getUserProfile().getEmail(),
                emailUtil.buildEmail(confirmationToken.getUserProfile().getFirstname(),
                        Constant.EMAIL_CHANGE_PASSWORD_CONTENT_TITLE,
                        Constant.EMAIL_CHANGE_PASSWORD_CONTENT_DESCRIPTION,
                        Constant.EMAIL_CHANGE_PASSWORD_LINK_DESCRIPTION,
                        link
                ),
                Constant.EMAIL_CHANGE_PASSWORD_SUBJECT
        );
    }

    @Async
    public void send(String to, String email, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            MimeBodyPart mimeBodyPart = new MimeBodyPart();

            mimeBodyPart.setContent(email, "text/html");
            mimeMultipart.addBodyPart(mimeBodyPart);

            mimeBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource("src/main/resources/logo.png");
            mimeBodyPart.setDataHandler(new DataHandler(fds));
            mimeBodyPart.setHeader("Content-ID", "<image>");
            mimeMultipart.addBodyPart(mimeBodyPart);

            mimeMessage.setFrom(Constant.EMAIL_SENDER);
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(mimeMultipart);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }
}
