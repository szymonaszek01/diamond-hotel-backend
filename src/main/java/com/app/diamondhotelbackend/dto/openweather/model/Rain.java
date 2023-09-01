package com.app.diamondhotelbackend.dto.openweather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Rain {

    @JsonProperty("1h")
    private String value;
}
