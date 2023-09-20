package com.app.diamondhotelbackend.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationPropertiesUtil {

    private String secretKey;

    private String accessTokenExpiration;

    private String confirmationTokenExpiration;

    private String refreshTokenExpiration;

    private String clientUri;

    private String serverUri;

    private String openWeatherSecretKey;

    private String stripeSecretKey;
}
