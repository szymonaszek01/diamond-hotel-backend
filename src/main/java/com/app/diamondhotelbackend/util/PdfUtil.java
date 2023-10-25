package com.app.diamondhotelbackend.util;

import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PdfUtil {

    private final SpringTemplateEngine springTemplateEngine;

    public InputStreamResource getReservationPdf(Reservation reservation, List<ReservedRoom> reservedRoomList, byte[] qrCode) {
        Context context = new Context();
        context.setVariable("reservation", reservation);
        context.setVariable("reservedRoomList", reservedRoomList);
        context.setVariable("qrCode", Base64.encodeBase64String(qrCode));
        String htmlToString = springTemplateEngine.process("reservationpdf", context);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlToString, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        return new InputStreamResource(byteArrayInputStream);
    }

    public InputStreamResource getPaymentForReservationPdf(Payment payment) {
        Context context = new Context();
        context.setVariable("payment", payment);
        String htmlToString = springTemplateEngine.process("paymentforreservationpdf", context);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlToString, byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        return new InputStreamResource(byteArrayInputStream);
    }
}
