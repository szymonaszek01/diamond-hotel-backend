package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String callbackUri = UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.OAUTH2_CALLBACK_URI)
                .queryParam(ConstantUtil.OAUTH2_ATTR_ERROR, UrlUtil.encode(ConstantUtil.INVALID_AUTH_PROVIDER_EXCEPTION))
                .build()
                .toUriString();

        response.sendRedirect(callbackUri);
    }
}
