package com.app.diamondhotelbackend.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlUtil {

    public static String decode(String value) {
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8)
                .replace("%2B", "+");
    }

    public static String encode(String value) {
        return URLEncoder.encode(value.replace("%2B", "+"), StandardCharsets.UTF_8)
                .replace("+", "%2B");
    }
}
