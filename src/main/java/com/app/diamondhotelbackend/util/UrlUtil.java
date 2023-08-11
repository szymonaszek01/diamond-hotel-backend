package com.app.diamondhotelbackend.util;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UrlUtil {

    public String decode(String value) {
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8)
                .replace("%2B", "+");
    }

    public String encode(String value) {
        return URLEncoder.encode(value.replace("%2B", "+"), StandardCharsets.UTF_8)
                .replace("+", "%2B");
    }
}
