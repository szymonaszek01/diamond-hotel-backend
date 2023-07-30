package com.app.diamondhotelbackend.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "base.uri")
public class BaseUriPropertiesProvider {

    private String client;

    private String server;
}
