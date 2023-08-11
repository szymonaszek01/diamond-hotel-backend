package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.util.BaseUriPropertiesProvider;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final BaseUriPropertiesProvider baseUriPropertiesProvider;

    private final UrlUtil urlUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String callbackUri = UriComponentsBuilder.fromUriString(baseUriPropertiesProvider.getClient() + Constant.OAUTH2_CALLBACK_URI)
                .queryParam(Constant.OAUTH2_ATTR_ERROR, urlUtil.encode(exception.getMessage()))
                .build()
                .toUriString();

        response.sendRedirect(callbackUri);
    }
}
