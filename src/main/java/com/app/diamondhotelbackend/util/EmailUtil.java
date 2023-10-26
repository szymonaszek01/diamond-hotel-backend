package com.app.diamondhotelbackend.util;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;

@EnableAsync
@Component
@AllArgsConstructor
public class EmailUtil {

    private final JavaMailSender mailSender;

    @Async
    public void send(String to, String email, String subject, InputStreamResource inputStreamResource, String attachmentName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            MimeBodyPart mimeBodyPart = new MimeBodyPart();

            mimeBodyPart.setContent(email, "text/html");
            mimeMultipart.addBodyPart(mimeBodyPart);

            mimeBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource("/app/src/main/resources/assets/logo-black.png");
            mimeBodyPart.setDataHandler(new DataHandler(dataSource));
            mimeBodyPart.setHeader("Content-ID", "<image>");
            mimeMultipart.addBodyPart(mimeBodyPart);

            if (inputStreamResource != null && attachmentName != null) {
                mimeBodyPart = new MimeBodyPart();
                dataSource = new ByteArrayDataSource(inputStreamResource.getInputStream(), "application/pdf");
                mimeBodyPart.setDataHandler(new DataHandler(dataSource));
                mimeBodyPart.setFileName(attachmentName);
                mimeMultipart.addBodyPart(mimeBodyPart);
            }

            mimeMessage.setFrom(ConstantUtil.EMAIL_SENDER);
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(mimeMultipart);

            mailSender.send(mimeMessage);

        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Failed to send email");
        }
    }
}
