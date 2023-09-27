package com.app.diamondhotelbackend.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class QrCodeUtil {

    public byte[] getQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream, matrixToImageConfig);

            return byteArrayOutputStream.toByteArray();

        } catch (WriterException | IOException e) {
            return new byte[0];
        }
    }
}
