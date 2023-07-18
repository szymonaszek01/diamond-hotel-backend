package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.OAuth2ProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.UserProfileService;
import com.app.diamondhotelbackend.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserProfileService userProfileService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        CustomOAuth2User customOAuth2User = CustomOAuth2User.builder().oauth2User(super.loadUser(userRequest)).build();
        if (customOAuth2User.getEmail().isEmpty()) {
            throw new OAuth2ProcessingException("Email not found from OAuth2 provider");
        }

        try {
            UserProfile userProfile = userProfileService.getUserProfileByEmail(customOAuth2User.getEmail());
            if (isAuthProviderMismatch(userProfile)) {
                throw new OAuth2ProcessingException("User not registered by OAuth2 provider");
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
        UserProfile userProfile = UserProfile.builder()
                .email(customOAuth2User.getEmail())
                .firstname(customOAuth2User.getGivenName())
                .lastname(customOAuth2User.getFamilyName())
                .picture(customOAuth2User.getPicture())
                .authProvider(Constant.OAUTH2)
                .role(Constant.USER)
                .build();

        userProfileService.saveUserProfile(userProfile);
    }
}
