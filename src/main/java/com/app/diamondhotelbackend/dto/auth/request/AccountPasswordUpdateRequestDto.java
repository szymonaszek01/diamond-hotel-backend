package com.app.diamondhotelbackend.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountPasswordUpdateRequestDto {

    private String email;

    @JsonProperty("new_password")
    private String newPassword;
}
