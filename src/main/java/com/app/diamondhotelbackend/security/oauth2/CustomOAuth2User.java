package com.app.diamondhotelbackend.security.oauth2;

import com.app.diamondhotelbackend.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oauth2User;

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute(Constant.OAUTH2_ATTR_NAME);
    }

    public String getGivenName() {
        return oauth2User.getAttribute(Constant.OAUTH2_ATTR_GIVEN_NAME);
    }

    public String getFamilyName() {
        return oauth2User.getAttribute(Constant.OAUTH2_ATTR_FAMILY_NAME);
    }

    public String getPicture() {
        return oauth2User.getAttribute(Constant.OAUTH2_ATTR_PICTURE);
    }

    public String getEmail() {
        return oauth2User.getAttribute(Constant.OAUTH2_ATTR_EMAIL);
    }
}
