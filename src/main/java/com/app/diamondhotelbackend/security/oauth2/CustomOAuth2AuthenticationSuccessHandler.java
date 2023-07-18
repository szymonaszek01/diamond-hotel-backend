package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.entity.Token;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.TokenService;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.OAuth2PropertiesProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2PropertiesProvider oAuth2PropertiesProvider;

    private final TokenService tokenService;

    private final UserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String callbackUri;

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            callbackUri = createCallbackUriOnSuccess(customOAuth2User);

        } catch (UserProfileProcessingException e) {
            callbackUri = createCallbackUriOnFailure(e);
        }

        response.sendRedirect(callbackUri);
    }

    private String createCallbackUriOnSuccess(CustomOAuth2User customOAuth2User) throws UserProfileProcessingException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(customOAuth2User.getEmail());
        Token token = tokenService.saveToken(userDetails);

        return UriComponentsBuilder.fromUriString(oAuth2PropertiesProvider.getCallbackUri())
                .queryParam(Constant.OAUTH2_ATTR_ACCESS_TOKEN, URLEncoder.encode(token.getAccessValue(), StandardCharsets.UTF_8))
                .queryParam(Constant.OAUTH2_ATTR_REFRESH_TOKEN, URLEncoder.encode(token.getRefreshValue(), StandardCharsets.UTF_8))
                .queryParam(Constant.OAUTH2_ATTR_EMAIL, URLEncoder.encode(token.getUserProfile().getEmail(), StandardCharsets.UTF_8))
                .build()
                .toUriString();
    }

    private String createCallbackUriOnFailure(UserProfileProcessingException e) {
        return UriComponentsBuilder.fromUriString(oAuth2PropertiesProvider.getCallbackUri())
                .queryParam(Constant.OAUTH2_ATTR_ERROR, URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8))
                .build()
                .toUriString();
    }
}
