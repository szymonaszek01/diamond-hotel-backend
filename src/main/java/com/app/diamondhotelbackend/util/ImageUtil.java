package com.app.diamondhotelbackend.util;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Component
public class ImageUtil {

    public byte[] getImageFromUrl(String urlText) {
        try {
            URL url = new URL(urlText);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            InputStream inputStream = url.openStream();
            byte[] buffer = new byte[Constant.MAX_IMAGE_SIZE];
            for (int size = 0; size != -1; size = inputStream.read(buffer)) {
                outputStream.write(buffer, 0, size);
            }

            return outputStream.toByteArray();

        } catch (IOException e) {
            return null;
        }
    }

    public byte[] compressImage(byte[] data) {
        try {
            Deflater deflater = new Deflater();
            deflater.setLevel(Deflater.BEST_COMPRESSION);
            deflater.setInput(data);
            deflater.finish();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[Constant.MAX_IMAGE_SIZE];
            while (!deflater.finished()) {
                int size = deflater.deflate(buffer);
                outputStream.write(buffer, 0, size);
            }
            outputStream.close();

            return outputStream.toByteArray();

        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    public byte[] decompressImage(byte[] data) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(data);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[Constant.MAX_IMAGE_SIZE];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();

            return outputStream.toByteArray();

        } catch (DataFormatException | IOException | NullPointerException e) {
            return null;
        }
    }
}
