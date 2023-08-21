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
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
@AllArgsConstructor
public class EmailUtil {

    private final JavaMailSender mailSender;

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
