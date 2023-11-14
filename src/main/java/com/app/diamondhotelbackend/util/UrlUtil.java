package com.app.diamondhotelbackend.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.springframework.security.util.FieldUtils.getFieldValue;

public class UrlUtil {

    public static String decode(String value) {
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8)
                .replace("%2B", "+");
    }

    public static String encode(String value) {
        return URLEncoder.encode(value.replace("%2B", "+"), StandardCharsets.UTF_8)
                .replace("+", "%2B");
    }

    public static String getValueFromJSONObject(String key, JSONObject jsonObject) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public static byte[] toImageAsByteArrayMapper(String url) {
        try {
            URL imageUrl = new URL(url);
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static List<Sort.Order> toOrderListMapper(JSONArray jsonArray) {
        List<Sort.Order> orderList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String name = "";
            String value = "";
            String related = "";

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                name = jsonObject.getString("name");
                value = jsonObject.getString("value");
                related = jsonObject.getString("related");

            } catch (JSONException ignored) {
            }

            if (name.isEmpty() || (name = toCamelCaseMapper(name)) == null || value.isEmpty()) {
                continue;
            }

            orderList.add(new Sort.Order(toSortDirectionMapper(value), related.length() > 0 ? related + "_" + name : name));
        }

        return orderList;
    }

    public static Sort.Direction toSortDirectionMapper(String value) {
        return "ASC".equals(value) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public static String toCamelCaseMapper(String value) {
        String[] parts = value.split("_");
        if (parts.length < 1) {
            return null;
        }

        StringBuilder camelCaseString = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1).toLowerCase());
        }

        return camelCaseString.toString();
    }

    public static boolean allNotNull(Object object, List<String> toOmitFieldList) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .filter(f -> !toOmitFieldList.contains((f.getName())))
                .map(f -> {
                    try {
                        return getFieldValue(object, f.getName());
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .noneMatch(Objects::isNull);
    }
}
