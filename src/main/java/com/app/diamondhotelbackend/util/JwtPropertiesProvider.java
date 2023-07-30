package com.app.diamondhotelbackend.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtPropertiesProvider {

    private String secretKey;

    private String accessTokenExpiration;

    private String confirmationTokenExpiration;

    private String refreshTokenExpiration;
}
