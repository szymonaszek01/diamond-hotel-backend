package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.authtoken.AuthTokenServiceImpl;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthTokenServiceImpl authTokenService;

    private final UserDetailsService userDetailsService;

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

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
        AuthToken authToken = authTokenService.createAuthToken(userDetails);

        return UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.OAUTH2_CALLBACK_URI)
                .queryParam(ConstantUtil.OAUTH2_ATTR_ACCESS_TOKEN, UrlUtil.encode(authToken.getAccessValue()))
                .queryParam(ConstantUtil.OAUTH2_ATTR_REFRESH_TOKEN, UrlUtil.encode(authToken.getRefreshValue()))
                .queryParam(ConstantUtil.OAUTH2_ATTR_ID, UrlUtil.encode(String.valueOf(authToken.getUserProfile().getId())))
                .queryParam(ConstantUtil.OAUTH2_ATTR_EMAIL, UrlUtil.encode(authToken.getUserProfile().getEmail()))
                .queryParam(ConstantUtil.OAUTH2_ATTR_CONFIRMED, authToken.getUserProfile().isAccountConfirmed())
                .build()
                .toUriString();
    }

    private String createCallbackUriOnFailure(UserProfileProcessingException e) {
        return UriComponentsBuilder.fromUriString(applicationPropertiesUtil.getClientUri() + ConstantUtil.OAUTH2_CALLBACK_URI)
                .queryParam(ConstantUtil.OAUTH2_ATTR_ERROR, UrlUtil.encode(e.getMessage()))
                .build()
                .toUriString();
    }
}
