package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.OAuth2PropertiesProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final OAuth2PropertiesProvider oAuth2PropertiesProvider;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String callbackUri = UriComponentsBuilder.fromUriString(oAuth2PropertiesProvider.getCallbackUri())
                .queryParam(Constant.OAUTH2_ATTR_ERROR, URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8))
                .build()
                .toUriString();

        response.sendRedirect(callbackUri);
    }
}
