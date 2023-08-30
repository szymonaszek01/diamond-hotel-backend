package com.app.diamondhotelbackend.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountEmailUpdateRequestDto {

    private String email;

    @JsonProperty("new_email")
    private String newEmail;
}
