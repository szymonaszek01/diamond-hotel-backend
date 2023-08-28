package com.app.diamondhotelbackend.service.oauth2;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.security.oauth2.CustomOAuth2User;
import com.app.diamondhotelbackend.service.confirmationtoken.ConfirmationTokenServiceImpl;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl extends DefaultOAuth2UserService implements OAuth2Service {

    private final UserProfileServiceImpl userProfileService;

    private final ConfirmationTokenServiceImpl confirmationTokenService;

    private final EmailServiceImpl emailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        CustomOAuth2User customOAuth2User = CustomOAuth2User.builder().oauth2User(super.loadUser(userRequest)).build();
        if (customOAuth2User.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException(Constant.EMAIL_NOT_FOUND_FROM_OAUTH_2_PROVIDER_EXCEPTION);
        }

        try {
            UserProfile userProfile = userProfileService.getUserProfileByEmail(customOAuth2User.getEmail());
            if (isAuthProviderMismatch(userProfile)) {
                throw new OAuth2AuthenticationException(Constant.INVALID_AUTH_PROVIDER_EXCEPTION);
            }

        } catch (UserProfileProcessingException e) {
            createAndSaveUserProfile(customOAuth2User);
        }

        return customOAuth2User;
    }

    private boolean isAuthProviderMismatch(UserProfile userProfile) {
        return !userProfile.getAuthProvider().equals(Constant.OAUTH2);
    }

    private void createAndSaveUserProfile(CustomOAuth2User customOAuth2User) {
        byte[] image = ImageUtil.getImageFromUrl(customOAuth2User.getPicture());

        UserProfile userProfile = UserProfile.builder()
                .email(customOAuth2User.getEmail())
                .firstname(customOAuth2User.getGivenName())
                .lastname(customOAuth2User.getFamilyName())
                .authProvider(Constant.OAUTH2)
                .role(Constant.USER)
                .accountConfirmed(false)
                .picture(image)
                .build();
        userProfileService.createUserProfile(userProfile);

        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(userProfile);
        emailService.sendConfirmationAccountEmail(confirmationToken);
    }
}
